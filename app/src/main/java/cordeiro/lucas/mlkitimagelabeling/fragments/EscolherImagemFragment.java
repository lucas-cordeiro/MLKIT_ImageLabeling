package cordeiro.lucas.mlkitimagelabeling.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import cordeiro.lucas.mlkitimagelabeling.MainActivity;
import cordeiro.lucas.mlkitimagelabeling.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EscolherImagemFragment extends Fragment {

    public static final String TAG = "ESCOLHER_IMAGEM";

    private Button btnImage;

    public EscolherImagemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ((MainActivity)getActivity()).setEnableButton(false);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_escolher_imagem, container, false);
        btnImage = view.findViewById(R.id.btnImage);

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                SelecionarImagemFragment fragment = new SelecionarImagemFragment();
                fragmentTransaction.setCustomAnimations( R.animator.left_in, R.animator.left_out , R.animator.right_in, R.animator.right_out);
                fragmentTransaction.replace(R.id.frameContainer, fragment, EscolherImagemFragment.TAG);
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

}
