package mjw.smile;

import smile.deep.tensor.Device;

/**
 *
 *
 * @author Jiawei Mao
 * @version 1.0.0
 * @since 02 Feb 2026, 5:11 PM
 */
public class MnistDemo {
    static void main() {
        Device device = Device.preferredDevice();
        device.setDefaultDevice();

        System.out.println(device);
    }
}
