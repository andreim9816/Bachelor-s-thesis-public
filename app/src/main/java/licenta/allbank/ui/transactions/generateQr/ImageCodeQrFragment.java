package licenta.allbank.ui.transactions.generateQr;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.google.zxing.WriterException;

import org.joda.time.DateTime;

import java.io.OutputStream;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import licenta.allbank.R;
import licenta.allbank.data.model.allbank.others.QrData;
import licenta.allbank.utils.DateFormatGMT;
import licenta.allbank.utils.ImageSaver;
import licenta.allbank.utils.messages.MessageDisplay;

import static licenta.allbank.ui.transactions.generateQr.GenerateCodeQrFragment.QR_DATA;
import static licenta.allbank.utils.messages.QrMessage.ERROR_GENERATING;
import static licenta.allbank.utils.messages.QrMessage.ERROR_SHARE_QR_CODE;

public class ImageCodeQrFragment extends Fragment {
    private QrData qrData;
    private ImageView qrCodeImageView;
    private Button shareButton, saveButton;
    private Bitmap bitmap;
    private Uri uriLastQrFile = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            qrData = bundle.getParcelable(QR_DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image_code_qr, container, false);

        /* Init Ui elements */
        initUiElements(view);

        /* Init image view qr code */
        initQrCode();

        /* Init behaviours */
        initBehaviours();

        return view;
    }

    private void initUiElements(View view) {
        qrCodeImageView = view.findViewById(R.id.image_view_image_code_qr);
        saveButton = view.findViewById(R.id.button_image_cod_qr_save);
        shareButton = view.findViewById(R.id.button_image_cod_qr_share);
    }

    private void initQrCode() {

        /* Dimension of the code */
        int dimen = 600;

        /* String to be encoded */
        String textString = createQrCodeString();

        QRGEncoder qrgEncoder = new QRGEncoder(textString, null, QRGContents.Type.TEXT, dimen);

        try {
            bitmap = qrgEncoder.encodeAsBitmap();
            qrCodeImageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            MessageDisplay.showLongMessage(getContext(), ERROR_GENERATING);
        }
    }

    private void initBehaviours() {
        initSaveButton();
        initShareButton();
    }

    private void initSaveButton() {
        String imageTitle = generateImageName();
        saveButton.setOnClickListener(v -> saveImage(bitmap, imageTitle));
    }

    private void initShareButton() {
        shareButton.setOnClickListener(v -> {
            ContentResolver resolver = requireContext().getContentResolver();
            ContentValues contentValues = new ContentValues();
            String fileName = "temp_" + generateImageName();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
            Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            uriLastQrFile = uri;

            try {
                OutputStream fileOutputStream = resolver.openOutputStream(uri);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/png");
                String message = generateStringMessageOnShare(qrData.getAmount(), qrData.getCurrency());
                share.putExtra(Intent.EXTRA_TEXT, message);
                share.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(share);
            } catch (Exception e) {
                MessageDisplay.showLongMessage(getContext(), ERROR_SHARE_QR_CODE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ContentResolver resolver = requireContext().getContentResolver();
        if (uriLastQrFile != null) {
            resolver.delete(uriLastQrFile, null, null);
            uriLastQrFile = null;
        }
    }

    private String createQrCodeString() {
        return "creditorIban=" + qrData.getCreditorIban() +
                "&creditorFirstName=" + qrData.getFirstName() +
                "&creditorLastName=" + qrData.getLastName() +
                "&amount=" + qrData.getAmount() +
                "&currency=" + qrData.getCurrency() +
                "&details=" + qrData.getDetails();
    }

    private void saveImage(Bitmap bitmap, String imageTitle) {
        ImageSaver.saveImage(getContext(), bitmap, imageTitle);
    }

    private String generateImageName() {
        float amount = qrData.getAmount();
        String time = DateFormatGMT.convertDateToStringFileName(new DateTime());
        return "QR_" + amount + "_" + time;
    }

    private String generateStringMessageOnShare(float amount, String currency) {
        return "Hi! You can send me " + amount + " " + currency + " by scanning this QR Code!";
    }
}