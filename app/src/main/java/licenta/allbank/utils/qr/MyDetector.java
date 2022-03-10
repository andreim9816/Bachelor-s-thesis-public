package licenta.allbank.utils.qr;

import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;

public class MyDetector extends Detector<Barcode> {
    private Detector<Barcode> mDelegate;

    MyDetector(Detector<Barcode> delegate) {
        mDelegate = delegate;
    }

    public SparseArray<Barcode> detect(Frame frame) {
        // *** crop the frame here
        Frame croppedFrame = frame; //TODO
        return mDelegate.detect(croppedFrame);
    }

    public boolean isOperational() {
        return mDelegate.isOperational();
    }

    public boolean setFocus(int id) {
        return mDelegate.setFocus(id);
    }
}