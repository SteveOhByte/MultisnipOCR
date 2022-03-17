package io.steveoh.multisnip;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

public class OCR {
    public String execute(String imagePath) {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(Multisnip.workingDirectory + Multisnip.fileSeparator + "tessdata");
        tesseract.setLanguage(Multisnip.lang);

        try {
            return tesseract.doOCR(new File(imagePath));
        }
        catch (TesseractException e) {
            throw new RuntimeException(e);
        }
    }
}
