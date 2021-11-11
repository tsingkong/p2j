package p2j.file.access.image.converters;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;

import p2j.file.access.image.writers.FileWriter;

public class PDFToJPEGConverter implements Converter {

    private int startPage;
    private int endPage;
    private String destSrc;
    private String prefixImageName;
    private PdfRenderer pdfRenderer;
    private File baseSrc = Environment.getExternalStorageDirectory();

    public PDFToJPEGConverter(PdfRenderer pdfRenderer, String destSrc, int startPage, int endPage, String prefixImageName) {
        this.startPage = startPage;
        this.endPage = endPage;
        this.pdfRenderer = pdfRenderer;
        this.destSrc = destSrc;
        this.prefixImageName = prefixImageName;
    }

    @Override
    public void convert() {
        String folderPath = createFolderIfNotExists(this.destSrc);
        for (int i = this.startPage; i < this.endPage; i++) {
            try {
                PdfRenderer.Page page = pdfRenderer.openPage(i);

                int factor = 4;
                Bitmap newPageBitmap = createBitmap(page.getWidth()*factor, page.getHeight()*factor);
                createCanvas(newPageBitmap);

                page.render(newPageBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT);

                new FileWriter(newPageBitmap, folderPath + File.separator + prefixImageName + "page-" + i + ".jpg").write();
                page.close();
            } catch (Exception e) {
                Log.e(getClass().getName(), "Could not create image for the page [" + i + "]", e);
            }
        }
    }

    @NonNull
    private String createFolderIfNotExists(String src) {
        String folderPath = src.split(".pdf")[0];
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder.getAbsolutePath();
    }

    private Bitmap createBitmap(int width, int height) {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        return Bitmap.createBitmap(width, height, conf);
    }

    private Canvas createCanvas(Bitmap mBitmap) {
        Canvas canvas = new Canvas(mBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(mBitmap, 0, 0, null);

        return canvas;
    }
}
