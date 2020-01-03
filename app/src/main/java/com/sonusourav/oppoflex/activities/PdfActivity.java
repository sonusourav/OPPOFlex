package com.sonusourav.oppoflex.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.shockwave.pdfium.PdfDocument;
import com.sonusourav.oppoflex.R;
import java.io.File;
import java.util.List;

public class PdfActivity extends AppCompatActivity
    implements OnPageChangeListener, OnLoadCompleteListener {

  PDFView pdfView;
  Integer pageNumber = 0;
  String pdfFileName;
  String TAG="PDFActivity";

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pdf);
    init();
  }

  private void init(){
    pdfView= (PDFView)findViewById(R.id.pdf_view);
    pdfFileName = getIntent().getStringExtra("filePath");
    Toast.makeText(getApplicationContext(),pdfFileName,Toast.LENGTH_SHORT);
    Log.d(TAG,pdfFileName);
    if(pdfFileName!=null){
      displayFromSdcard(pdfFileName);
    }
    else{
      startActivity(new Intent(PdfActivity.this, MainActivity.class));
      Toast.makeText(getApplicationContext(),"File Does not exist",Toast.LENGTH_SHORT).show();
    }
  }

  private void displayFromSdcard(String path) {

    File file = new File(path);

    if(file.exists()){
      Log.d(TAG,path);
      pdfView.fromFile(file)
          .defaultPage(pageNumber)
          .enableSwipe(true)
          .swipeHorizontal(false)
          .onPageChange(this)
          .enableAnnotationRendering(true)
          .onLoad(this)
          .scrollHandle(new DefaultScrollHandle(this))
          .load();
    }else{
      Toast.makeText(getApplicationContext(),"File Does not exist",Toast.LENGTH_SHORT).show();
    }


  }
  @Override
  public void onPageChanged(int page, int pageCount) {
    pageNumber = page;
    setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
  }
  @Override
  public void loadComplete(int nbPages) {
    PdfDocument.Meta meta = pdfView.getDocumentMeta();
    printBookmarksTree(pdfView.getTableOfContents(), "-");

  }

  public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
    for (PdfDocument.Bookmark b : tree) {

      Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

      if (b.hasChildren()) {
        printBookmarksTree(b.getChildren(), sep + "-");
      }
    }
  }

  public void onBackPressed() {
    NavUtils.navigateUpFromSameTask(this);
  }

}