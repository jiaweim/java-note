package mjw.swing;

import javax.swing.*;
import java.util.List;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 16 Jan 2024, 8:03 PM
 */
public class RolloverSpinnerListModel extends SpinnerListModel
{
    public RolloverSpinnerListModel(List<?> values) {
        super(values);
    }

    public RolloverSpinnerListModel(Object[] values) {
        super(values);
    }

    @Override
    public Object getNextValue() {
        Object nextValue = super.getNextValue();
        if (nextValue == null) {
            nextValue = getList().get(0);
        }
        return nextValue;
    }

    @Override
    public Object getPreviousValue() {
        Object previousValue = super.getPreviousValue();
        if (previousValue == null) {
            List<?> list = getList();
            previousValue = list.get(list.size() - 1);
        }
        return previousValue;
    }
}
