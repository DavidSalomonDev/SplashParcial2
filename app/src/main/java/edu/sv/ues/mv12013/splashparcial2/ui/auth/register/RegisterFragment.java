package edu.sv.ues.mv12013.splashparcial2.ui.auth.register;

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
import edu.sv.ues.mv12013.splashparcial2.ui.auth.AuthActivity;
import edu.sv.ues.mv12013.splashparcial2.ui.auth.login.LoginFragment;
import edu.sv.ues.mv12013.splashparcial2.ui.common.UiState;

public class RegisterFragment extends Fragment {

    private RegisterViewModel vm;
    private EditText etEmail, etName, etPassword;
    private ProgressBar progress;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        vm = new ViewModelProvider(this).get(RegisterViewModel.class);
        etEmail = v.findViewById(R.id.etEmail);
        etName = v.findViewById(R.id.etName);
        etPassword = v.findViewById(R.id.etPassword);
        progress = v.findViewById(R.id.progress);

        v.findViewById(R.id.btnRegister).setOnClickListener(view ->
                vm.register(etEmail.getText().toString().trim(), etName.getText().toString().trim(), etPassword.getText().toString())
        );

        vm.uiState.observe(getViewLifecycleOwner(), state -> {
            progress.setVisibility(state == UiState.Loading ? View.VISIBLE : View.GONE);
            if (state instanceof UiState.Error) {
                Toast.makeText(requireContext(), ((UiState.Error) state).message, Toast.LENGTH_SHORT).show();
            }
            // Al registrar, volver a Login
            if (state == UiState.Success) {
                ((AuthActivity) requireActivity()).replace(new LoginFragment(), false);
            }
        });
    }
}