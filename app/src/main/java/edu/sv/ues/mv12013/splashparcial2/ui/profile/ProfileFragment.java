package edu.sv.ues.mv12013.splashparcial2.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import edu.sv.ues.mv12013.splashparcial2.R;
import edu.sv.ues.mv12013.splashparcial2.ui.common.UiState;

public class ProfileFragment extends Fragment {

    private ProfileViewModel vm;
    private EditText etName;
    private ProgressBar progress;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        vm = new ViewModelProvider(this).get(ProfileViewModel.class);
        etName = v.findViewById(R.id.etName);
        progress = v.findViewById(R.id.progress);

        v.findViewById(R.id.btnSave).setOnClickListener(view ->
                vm.updateName(etName.getText().toString())
        );

        vm.name.observe(getViewLifecycleOwner(), name -> etName.setText(name));
        vm.uiState.observe(getViewLifecycleOwner(), state -> {
            progress.setVisibility(state == UiState.Loading ? View.VISIBLE : View.GONE);
            if (state instanceof UiState.Error) {
                Toast.makeText(requireContext(), ((UiState.Error) state).message, Toast.LENGTH_SHORT).show();
            }
            if (state == UiState.Success) {
                Toast.makeText(requireContext(), "Guardado. Se sincronizará si estás offline.", Toast.LENGTH_SHORT).show();
            }
        });

        vm.loadCurrentUser();
    }
}