package cordeiro.lucas.mlkitimagelabeling;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.LongDef;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions;

import java.util.List;

import cordeiro.lucas.mlkitimagelabeling.fragments.EscolherImagemFragment;
import cordeiro.lucas.mlkitimagelabeling.util.PermissaoUtil;

public class MainActivity extends AppCompatActivity {

    private String[] permissoes = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
    private final int RC_PERMISSOES=100;
    private FrameLayout container;
    private Button btnAvaliar;
    private ImageView imageView;
    private ProgressBar progressBar;
    private TextView textView;
    private Bitmap bitmap;
    private boolean escolhendoImagem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        container = findViewById(R.id.frameContainer);
        btnAvaliar = findViewById(R.id.btnAnalisar);
        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.txtLabel);

        verificarPermissoes();
        carregarFragment();

        btnAvaliar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bitmap!=null)
                analisarImagem();
                else
                    Toast.makeText(MainActivity.this, "Selecione uma imagem!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onBackPressed() {
        if(escolhendoImagem)
            carregarFragment();
        else
            super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(RC_PERMISSOES == requestCode){
            boolean permissoesAceitas = true;
            for(int permissaoResultado : grantResults){
                if(permissaoResultado == PackageManager.PERMISSION_DENIED){
                    permissoesAceitas = false;
                    break;
                }
            }

            if(permissoesAceitas)
                Toast.makeText(MainActivity.this, "Escolha uma imagem para analisar!", Toast.LENGTH_SHORT).show();
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Permissões");
                builder.setMessage("É necessário aceitar as permissões para uso do aplicativo.");
                builder.setCancelable(false);
                builder.setPositiveButton("Aceitar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dialog.dismiss();
                        verificarPermissoes();
                    }
                });
                builder.setNegativeButton("Negar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dialog.dismiss();
                        finish();
                    }
                });
                builder.create().show();
            }
        }
    }

    private void verificarPermissoes() {
        if(PermissaoUtil.validarPermissoes(permissoes, MainActivity.this, RC_PERMISSOES)){
            Toast.makeText(MainActivity.this, "Escolha uma imagem para analisar!", Toast.LENGTH_SHORT).show();
        }
    }

    private void carregarFragment() {
        escolhendoImagem = false;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        EscolherImagemFragment fragment = new EscolherImagemFragment();
        fragmentTransaction.setCustomAnimations(R.animator.right_in, R.animator.right_out, R.animator.left_in, R.animator.left_out);
        fragmentTransaction.replace(container.getId(), fragment, EscolherImagemFragment.TAG);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    private void analisarImagem() {
        progressBar.setVisibility(View.VISIBLE);
        setEnableButton(false);
        FirebaseVisionLabelDetectorOptions options =
                new FirebaseVisionLabelDetectorOptions.Builder()
                        .setConfidenceThreshold(0.8f)
                        .build();

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionLabelDetector detector = FirebaseVision.getInstance()
                .getVisionLabelDetector(options);
        Task<List<FirebaseVisionLabel>> result =
                detector.detectInImage(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<FirebaseVisionLabel>>() {
                                    @Override
                                    public void onSuccess(List<FirebaseVisionLabel> labels) {
                                        progressBar.setVisibility(View.GONE);
                                        setEnableButton(true);
                                        String text = "";
                                        for (FirebaseVisionLabel label: labels) {
                                            text += "\n"+label.getLabel();
                                        }
                                        textView.setText(text);
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressBar.setVisibility(View.GONE);
                                        setEnableButton(true);
                                       Toast.makeText(MainActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
    }


    public void setEnableButton(boolean enable){
        btnAvaliar.setEnabled(enable);
    }

    public void setEscolhendoImagem(boolean escolhendoImagem) {
        this.escolhendoImagem = escolhendoImagem;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        imageView.setImageBitmap(bitmap);
        textView.setText("Imagem Selecionada!");
        setEnableButton(true);
    }
}
