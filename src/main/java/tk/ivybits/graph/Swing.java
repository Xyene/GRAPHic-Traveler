package tk.ivybits.graph;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;

public class Swing {
    public static void toClipboard(Image image) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new TransferableImage(image), new ClipboardOwner() {
            @Override
            public void lostOwnership(Clipboard clipboard, Transferable contents) {
            }
        });
    }

    private static class TransferableImage implements Transferable {
        private final Image i;

        public TransferableImage(Image i) {
            this.i = i;
        }

        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (flavor.equals(DataFlavor.imageFlavor) && i != null) {
                return i;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }

        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.imageFlavor};
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            DataFlavor[] flavors = getTransferDataFlavors();
            for (int i = 0; i < flavors.length; i++) {
                if (flavor.equals(flavors[i])) {
                    return true;
                }
            }
            return false;
        }
    }
}
