package edu.sv.ues.mv12013.splashparcial2.ui.auth.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import edu.sv.ues.mv12013.splashparcial2.R;
import edu.sv.ues.mv12013.splashparcial2.ui.auth.AuthActivity;
import edu.sv.ues.mv12013.splashparcial2.ui.auth.register.RegisterFragment;
import edu.sv.ues.mv12013.splashparcial2.ui.common.UiState;
import edu.sv.ues.mv12013.splashparcial2.ui.home.HomeActivity;

public class LoginFragment extends Fragment {

    private LoginViewModel vm;
    private EditText etEmail, etPassword;
    private CheckBox cbRemember;
    private ProgressBar progress;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        vm = new ViewModelProvider(this).get(LoginViewModel.class);
        etEmail = v.findViewById(R.id.etEmail);
        etPassword = v.findViewById(R.id.etPassword);
        cbRemember = v.findViewById(R.id.cbRemember);
        progress = v.findViewById(R.id.progress);

        v.findViewById(R.id.btnLogin).setOnClickListener(view ->
                vm.login(etEmail.getText().toString().trim(), etPassword.getText().toString(), cbRemember.isChecked())
        );

        v.findViewById(R.id.tvRegister).setOnClickListener(view -> {
            ((AuthActivity) requireActivity()).replace(new RegisterFragment(), true);
        });

        vm.uiState.observe(getViewLifecycleOwner(), state -> {
            if (state == UiState.Loading) progress.setVisibility(View.VISIBLE);
            else progress.setVisibility(View.GONE);

            if (state instanceof UiState.Error) {
                Toast.makeText(requireContext(), ((UiState.Error) state).message, Toast.LENGTH_SHORT).show();
            }
            if (state == UiState.Success) {
                startActivity(new Intent(requireContext(), HomeActivity.class));
                requireActivity().finish();
            }
        });
    }
}