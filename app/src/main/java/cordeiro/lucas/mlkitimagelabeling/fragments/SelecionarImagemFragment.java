package cordeiro.lucas.mlkitimagelabeling.fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

import cordeiro.lucas.mlkitimagelabeling.MainActivity;
import cordeiro.lucas.mlkitimagelabeling.R;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelecionarImagemFragment extends Fragment {

    public static final String TAG = "ESCOLHER_IMAGEM";

    private Button btnGaleria, btnCamera;
    private final int RC_CAMERA=200, RC_GALERIA=300;

    public SelecionarImagemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_selecionar_imagem, container, false);

        btnCamera = view.findViewById(R.id.btnCamera);
        btnGaleria = view.findViewById(R.id.btnGaleria);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(camera.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(camera, RC_CAMERA);
                }
            }
        });

        btnGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galeria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(galeria.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(galeria, RC_GALERIA);
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(getActivity()!=null)
        ((MainActivity)getActivity()).setEscolhendoImagem(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(getActivity()!=null)
        ((MainActivity)getActivity()).setEscolhendoImagem(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(getActivity()!=null && resultCode == RESULT_OK){
            if(requestCode == RC_CAMERA){
                Bundle extras = data.getExtras();
                Bitmap imagem = (Bitmap) extras.get("data");
                ((MainActivity)getActivity()).setBitmap(imagem);

            }else if(requestCode == RC_GALERIA){
                try {
                    Bitmap imagem = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                    ((MainActivity)getActivity()).setBitmap(imagem);
                } catch (IOException e) {
                    Log.d(TAG, "Error: "+e.getMessage());
                    Toast.makeText(getContext(), "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }else{
            Toast.makeText(getContext(), "Falha ao recuperar imagem!", Toast.LENGTH_SHORT).show();
        }
    }
}
