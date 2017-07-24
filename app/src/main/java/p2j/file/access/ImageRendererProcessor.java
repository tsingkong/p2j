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
    private String path;
    private Handler handler;
    private Runnable runnable;

    private ForkJoinPool forkJoinPool = ApplicationModule.provideForkJoinPool();
    private PdfRenderer pdfRenderer;

    public ImageRendererProcessor(String path , Handler handler, Runnable runnable) {
        this.path = path;
        this.handler = handler;
        this.runnable = runnable;
        this.pdfRenderer = ApplicationModule.providePdfRenderer(path);
    }

    public void execute() {
            forkJoinPool.submit(new ImageRendererAction(pdfRenderer,
                                                        this.path,
                                                        Runtime.getRuntime().availableProcessors(),
                                                        0,
                                                        pdfRenderer.getPageCount(),
                                                        true));

            this.handler.removeCallbacks(this.runnable);
    }
}
