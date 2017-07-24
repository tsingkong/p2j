package p2j.configuration;

import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

public class ApplicationModule {
    public static ForkJoinPool provideForkJoinPool() {
        int executorPoolSize = Runtime.getRuntime().availableProcessors();
        return new ForkJoinPool(executorPoolSize);
    }

    public static PdfRenderer providePdfRenderer(String src) {
        try {
            return new PdfRenderer(getSeekableFileDescriptor(src));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ParcelFileDescriptor getSeekableFileDescriptor(String src) {
        ParcelFileDescriptor fd = null;
        File pdfFile = new File(src);
        pdfFile.setReadable(true);
        try {
            fd = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fd;
    }
}
