package com.example.savephoto;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * 在一般情況下，運行沒有任何問題;可是當把targetSdkVersion指定成24及之上並且在API> = 24的設備上運行時，會拋出異常：
 * 總而言之，就是Android不再允許在app中把file://Uri暴露給其他app，包括但不侷限於通過Intent或ClipData等方法。
 * 因此，Google提供了FileProvider
 * */
public class MainActivity extends AppCompatActivity {
    private Button button;
    private ImageView imageView;
    private Uri uri;
    private String fileName;
    private final static int CAMARA_CODE=500;
    private final static int PERMISSION_REQUEST_CODE=0;
    private final String[] permissions = new String[]{
            //讀取權限
            Manifest.permission.READ_EXTERNAL_STORAGE,
            //寫入權限
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView=(ImageView) findViewById(R.id.imageView);
        button=(Button) findViewById(R.id.button);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions,PERMISSION_REQUEST_CODE);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camara();
            }
        });

    }

    private void camara() {
        //圖片名稱，用時間格式(年月日時分秒)來命名，這樣的命名，就不會重覆的情況。
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        fileName = format.format(date);

        //創建目錄的路徑。
        String path =Environment.getExternalStorageDirectory().getPath()+"/Documents";
        //New出檔案路徑的File
        File saveImage = new File(path,fileName+".jpg");
        //先判斷是否有此檔案，再建立文字檔
        if (!saveImage.exists()) {
            try {
                saveImage.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else Log.e("note", "existsDir" );

        //將File對像轉換為Uri並啟動照相程序
        uri = FileProvider.getUriForFile(this, Environment.getExternalStorageDirectory().getPath()+"/Documents" , saveImage);
        //啟動照相功能
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        //指定圖片輸出地方。
        intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        //拍完照startActivityForResult() 結果返回onActivityResult() 函數
        startActivityForResult(intent,CAMARA_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //圖片解析成Bitmap
        Bitmap bitmap = null;
        Matrix m=new Matrix();
        //順時針旋轉90度
        m.postRotate(90);
        Log.e("111", "onActivityResult:" + uri );
            try {
                bitmap = BitmapFactory.decodeStream(
                        getContentResolver().openInputStream(uri));
                int width=bitmap.getWidth();
                //取得圖片的長度
                int height=bitmap.getHeight();
                bitmap=Bitmap.createBitmap(bitmap, 0, 0, width, height, m, true);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            //將照片顯示出來
            imageView.setImageBitmap(bitmap);

    }
}