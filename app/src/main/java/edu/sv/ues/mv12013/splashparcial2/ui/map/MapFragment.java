package edu.sv.ues.mv12013.splashparcial2.ui.map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import edu.sv.ues.mv12013.splashparcial2.R;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final LatLng PUNTO_REF = new LatLng(13.9946, -89.5590); // ejemplo Santa Ana

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().position(PUNTO_REF).title("Punto de referencia"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(PUNTO_REF, 15f));
    }
}