package p2j.file.access.image;

import android.graphics.pdf.PdfRenderer;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

import p2j.file.access.image.converters.PDFToJPEGConverter;

public class ImageRendererAction extends RecursiveAction {

    private PdfRenderer renderer;
    private String destSrc;
    private int numOfExecutors = 0;
    private int startPage = 0;
    private int endPage = 0;
    private String prefixImageName;
    private boolean isFanOut = false;
    private UUID threadId = UUID.randomUUID();

    private PDFToJPEGConverter pdfToJPEGConverter;

    public ImageRendererAction(PdfRenderer renderer,
                               String destSrc,
                               int numOfExecutors,
                               int startPage,
                               int endPage,
                               String prefixImageName,
                               boolean isFanOut) {
        this.renderer = renderer;
        this.destSrc = destSrc;
        this.numOfExecutors = numOfExecutors;
        this.startPage = startPage;
        this.endPage = endPage;
        this.prefixImageName = prefixImageName;
        this.isFanOut = isFanOut;
    }

    public ImageRendererAction(PdfRenderer renderer,
                               String destSrc,
                               int numOfExecutors,
                               int startPage,
                               int endPage,
                               String prefixImageName) {
        this.renderer = renderer;
        this.destSrc = destSrc;
        this.numOfExecutors = numOfExecutors;
        this.startPage = startPage;
        this.endPage = endPage;
        this.prefixImageName = prefixImageName;
        this.pdfToJPEGConverter = new PDFToJPEGConverter(renderer, destSrc, startPage, endPage, prefixImageName);
    }

    @Override
    protected void compute() {
        if (isFanOut) {
            List<ImageRendererAction> imageRendererActions = createSubtasks();

            for (ImageRendererAction imageRendererAction : imageRendererActions) {
                imageRendererAction.fork();
            }
        } else {
            long startTime = SystemClock.currentThreadTimeMillis();
            pdfToJPEGConverter.convert();
            long endTime = SystemClock.currentThreadTimeMillis();
            Log.i(getClass().getName(), "Time take to convert [" + (endPage - startPage) + "] in thread [" + threadId + "] is [" + (endTime - startTime) + "]");
        }
    }

    private List<ImageRendererAction> createSubtasks() {
        List<ImageRendererAction> subtasks = new ArrayList();
        int poolSize = this.endPage / numOfExecutors;

        for (int i = 0; i < poolSize; i++) {
            int startPage = this.startPage + (this.numOfExecutors * i);
            int endPage = this.startPage + (this.numOfExecutors * (i + 1)) - 1;

            subtasks.add(new ImageRendererAction(this.renderer,
                    this.destSrc,
                    this.numOfExecutors,
                    startPage,
                    endPage,
                    prefixImageName));
        }
        subtasks.add(new ImageRendererAction(this.renderer,
                this.destSrc,
                this.numOfExecutors,
                (endPage - (this.endPage % numOfExecutors)),
                endPage,
                prefixImageName));
        return subtasks;
    }
}