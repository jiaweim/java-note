package mjw.swing;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 05 Jan 2024, 1:34 PM
 */
public class SwingWorkerTest {

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            var frame = new SwingWorkerFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}

class SwingWorkerFrame extends JFrame {

    private JFileChooser chooser;
    private JTextArea textArea;
    private JLabel statusLine;
    private JMenuItem openItem;
    private JMenuItem cancelItem;
    private SwingWorker<StringBuilder, ProgressData> textReader;
    public static final int TEXT_ROWS = 20;
    public static final int TEXT_COLUMNS = 60;

    public SwingWorkerFrame() {
        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));

        textArea = new JTextArea(TEXT_ROWS, TEXT_COLUMNS);
        add(new JScrollPane(textArea));

        statusLine = new JLabel(" ");
        add(statusLine, BorderLayout.SOUTH);

        var menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        var menu = new JMenu("File");
        menuBar.add(menu);

        openItem = new JMenuItem("Open");
        menu.add(openItem);
        openItem.addActionListener(event -> {
            // show file chooser dialog
            int result = chooser.showOpenDialog(null);

            // if file selected, set it as icon of the label
            if (result == JFileChooser.APPROVE_OPTION) {
                textArea.setText("");
                openItem.setEnabled(false);
                textReader = new TextReader(chooser.getSelectedFile());
                textReader.execute(); // 执行 SwingWorker
                cancelItem.setEnabled(true);
            }
        });

        cancelItem = new JMenuItem("Cancel");
        menu.add(cancelItem);
        cancelItem.setEnabled(false);
        cancelItem.addActionListener(event -> textReader.cancel(true));
        pack();
    }

    private class ProgressData {

        public int number;
        public String line;
    }

    private class TextReader extends SwingWorker<StringBuilder, ProgressData> {

        private File file;
        private StringBuilder text = new StringBuilder();

        public TextReader(File file) {
            this.file = file;
        }

        // 后台线程执行
        public StringBuilder doInBackground() throws IOException, InterruptedException {
            int lineNumber = 0;
            try (var in = new Scanner(new FileInputStream(file), StandardCharsets.UTF_8)) {
                while (in.hasNextLine()) {
                    String line = in.nextLine();
                    lineNumber++;
                    text.append(line).append("\n");
                    var data = new ProgressData();
                    data.number = lineNumber;
                    data.line = line;
                    publish(data); // 每读取一行，用 publish 发布行号和文本
                    Thread.sleep(1); // to test cancellation; no need to do this in your programs
                }
            }
            return text;
        }

        // process 在 EDT 执行，参数为收集的 publish 数据
        @Override
        public void process(List<ProgressData> data) {
            if (isCancelled()) return;
            var builder = new StringBuilder();
            // 这里取最后一行的行号，更新状态栏
            statusLine.setText(String.valueOf(data.get(data.size() - 1).number));
            for (ProgressData d : data)
                builder.append(d.line).append("\n");
            textArea.append(builder.toString()); // 将文本发送到 TextArea
        }

        public void done() {
            try {
                StringBuilder result = get();
                textArea.setText(result.toString());// 使用完整文本更新 TextArea
                statusLine.setText("Done");
            } catch (InterruptedException ex) {

            } catch (CancellationException ex) {
                textArea.setText("");
                statusLine.setText("Cancelled");

            } catch (ExecutionException ex) {
                statusLine.setText("" + ex.getCause());

            }
            cancelItem.setEnabled(false);
            openItem.setEnabled(true);
        }
    }
}
