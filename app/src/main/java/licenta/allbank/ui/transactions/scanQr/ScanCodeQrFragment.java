package licenta.allbank.ui.transactions.scanQr;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import licenta.allbank.R;
import licenta.allbank.data.model.allbank.others.QrData;
import licenta.allbank.utils.Logging;

import static licenta.allbank.ui.home.HomeFragment.navigateToNextFragment;
import static licenta.allbank.ui.transactions.generateQr.GenerateCodeQrFragment.QR_DATA;

public class ScanCodeQrFragment extends Fragment {
    private static final int REQUEST_CAMERA_PERMISSION = 201;

    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private NavController navController;
    private QrData qrData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scan_code_qr, container, false);

        /* Init UI elements */
        initUiElements(view);

        initQr();
        return view;
    }

    private void initUiElements(View view) {
        this.surfaceView = view.findViewById(R.id.surface_view_transactions_scan_qr);
    }

    private void initQr() {
        /* Init barcode detector */
        initBarcodeDetector();

        /* Init camera source */
        initCameraSource();

        /* Init surface view */
        initSurfaceView();

        /* Init QR scanner logic  */
        initBarcodeProcessor();
    }

    private void initCameraSource() {
        cameraSource = new CameraSource.Builder(requireContext(), barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();
    }

    private void initBarcodeDetector() {
        barcodeDetector = new BarcodeDetector.Builder(requireContext())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
    }

    private void initBarcodeProcessor() {
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(@NotNull Detector.Detections<Barcode> detections) {
                /* Gets called every time the view input has changed */
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    if (barcodes.valueAt(0).valueFormat == Barcode.TEXT) {

                        /* Read data from QR code */
                        qrData = convertQrData(barcodes.valueAt(0).displayValue);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(QR_DATA, qrData);

                        /* Navigate to next fragment */
                        requireActivity().runOnUiThread(() -> {
                            if (Objects.requireNonNull(navController.getCurrentDestination()).getId() == R.id.navigation_transactions) {
                                navigateToNextFragment(navController, R.id.action_navigation_transactions_to_readTransactionQrFragment, bundle);
                            }
                        });
                    }
                }
            }
        });
    }

    private void initSurfaceView() {
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
    }

    /**
     * @param displayValues String with the information decoded from a QR code
     * @return QrData object built from the given string
     */
    public static QrData convertQrData(String displayValues) {
        String[] parts = displayValues.split("&");
        String creditorIban = parts[0].split("=")[1];
        String firstName = parts[1].split("=")[1];
        String lastName = parts[2].split("=")[1];
        float amount = Float.parseFloat(parts[3].split("=")[1]);
        String currency = parts[4].split("=")[1];
        String details = parts[5].split("=")[1];
        return new QrData(creditorIban, currency, details, firstName, lastName, amount);
    }

    @Override
    public void onStart() {
        super.onStart();
        Logging.show("QR", " --- ON START");
    }

    @Override
    public void onResume() {
        super.onResume();
        Logging.show("QR", " --- ON RESUME");
    }

    @Override
    public void onPause() {
        super.onPause();
        Logging.show("QR", " --- ON PAUSE");
//        cameraSource.release();
    }

    @Override
    public void onDestroy() {
        Logging.show("QR", " --- ON DESTROY");
        super.onDestroy();
        cameraSource.release();
    }
}