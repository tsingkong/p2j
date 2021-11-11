package p2j.file.access;

import android.graphics.pdf.PdfRenderer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

import p2j.configuration.ApplicationModule;
import p2j.file.access.image.ImageRendererAction;

public class ImageRendererProcessor {
    private String image_path;
    private String prefixImageName;
    private Handler handler;
    private Runnable runnable;

    private ForkJoinPool forkJoinPool = ApplicationModule.provideForkJoinPool();
    private PdfRenderer pdfRenderer;

    public ImageRendererProcessor(String pdf_path, String image_path, String prefixImageName, Handler handler, Runnable runnable) {
        this.image_path = image_path;
        this.prefixImageName = prefixImageName;
        this.handler = handler;
        this.runnable = runnable;
        this.pdfRenderer = ApplicationModule.providePdfRenderer(pdf_path);
    }

    public void execute() {
            forkJoinPool.submit(new ImageRendererAction(pdfRenderer,
                                                        this.image_path,
                                                        Runtime.getRuntime().availableProcessors(),
                                                        0,
                                                        pdfRenderer.getPageCount(),
                                                        prefixImageName,
                                                        true));

            this.handler.removeCallbacks(this.runnable);
    }
}
