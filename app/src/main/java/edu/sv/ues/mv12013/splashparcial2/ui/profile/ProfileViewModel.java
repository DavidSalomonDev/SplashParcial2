package edu.sv.ues.mv12013.splashparcial2.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import edu.sv.ues.mv12013.splashparcial2.ui.common.BaseViewModel;

public class ProfileViewModel extends BaseViewModel {
    private final MutableLiveData<String> _name = new MutableLiveData<>("");
    public LiveData<String> name = _name;

    public void loadCurrentUser() {
        // TODO: Cargar desde Room primero; si online, refrescar de Firestore
        _name.postValue("Usuario"); // placeholder
    }

    public void updateName(String newName) {
        setLoading();
        // TODO: si offline -> guardar PendingSync en Room; si online -> actualizar y marcar sincronizado
        if (newName == null || newName.trim().length() < 3) {
            setError("Nombre inválido");
            return;
        }
        _name.postValue(newName.trim());
        setSuccess();
    }
}