package se.helsingborg.oppna.solarie.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kalle
 * @since 2014-09-17 00:05
 */
public class OCR {

  private List<File> convertImageToOne300DPIGrayScaleImagePerPage(File imageFile, File outputPath) {

    List<File> pageImages = null;

    // todo Use GraphicsMagic to convert image to one 300 DPI gray scale image per page.


    return pageImages;

  }

  private String ocr(File image) {

    // todo Use Tesseract OCR to convert image to text

    return null;
  }

  public List<String> process(File imageFile) {

    File pagesPath = createTemporaryDirectory();
    try {

      List<File> pageImages = convertImageToOne300DPIGrayScaleImagePerPage(imageFile, pagesPath);

      List<String> textPages = new ArrayList<>(pageImages.size());
      for (File pageImage : pageImages) {
        textPages.add(ocr(pageImage));
      }

      return textPages;

    } finally {
      FileUtils.deleteQuietly(pagesPath);

    }

  }

  private File createTemporaryDirectory() {

    // todo

    return null;
  }


}
