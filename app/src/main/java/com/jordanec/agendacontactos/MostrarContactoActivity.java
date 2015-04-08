package com.jordanec.agendacontactos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MostrarContactoActivity extends ActionBarActivity implements View.OnClickListener{
    int tipoDeMuestraDeContacto;
    EditText etIDContacto;
    EditText etNombre;
    EditText etApellido1;
    EditText etApellido2;
    EditText etCelular;
    EditText etCasa;
    EditText etDireccion;
    Button btnCancelar;
    Button btnPositivo;
    Intent i;
    String nombreContacto;
    int iDContacto;
    Database database;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacto_datos);
        tipoDeMuestraDeContacto = getIntent().getIntExtra(getString(R.string.tipoDeMuestraDeContactoKey), 0);
        setDatos();
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mostrar_contacto, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/
    private void setDatos(){
        etIDContacto = (EditText) findViewById(R.id.etIDContacto);
        etNombre = (EditText) findViewById(R.id.etNombre);
        etApellido1 = (EditText) findViewById(R.id.etApellido1);
        etApellido2 = (EditText) findViewById(R.id.etApellido2);
        etCelular = (EditText) findViewById(R.id.etCelular);
        etCasa = (EditText) findViewById(R.id.etCasa);
        etDireccion = (EditText) findViewById(R.id.etDireccion);
        btnCancelar = (Button) findViewById(R.id.btnCancelar);
        btnPositivo = (Button) findViewById(R.id.btnPositivo);
        database = new Database(this);

        switch (tipoDeMuestraDeContacto){
            case 1:
                btnPositivo.setText(getString(R.string.agregar));
                btnCancelar.setOnClickListener(this);
                btnPositivo.setOnClickListener(this);
                break;
            case 2:
                iDContacto = getIntent().getIntExtra(getString(R.string.iDContactoKey), -1);
                mostrarContacto();
                deshabilitarYOcultar();
                break;
            case 3:
                handler = new Handler() {
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        Bundle b = msg.getData();
                        String s = b.getString(getString(R.string.toastKey));
                        Toast.makeText(MostrarContactoActivity.this, s, Toast.LENGTH_LONG).show();
                    }
                };
                btnPositivo.setText(getString(R.string.guardar));
                iDContacto = getIntent().getIntExtra(getString(R.string.iDContactoKey), -1);
                btnCancelar.setOnClickListener(this);
                btnPositivo.setOnClickListener(this);
                mostrarContacto();
                break;

        }

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnCancelar:
                //switch (tipoDeMuestraDeContacto){
                //    case 1:
                //        finish();
                //        break;
                //    case 2:
                //        finish();
                //        break;

                //}
                i = new Intent();//"com.jordan.MainActivity"
                setResult(RESULT_CANCELED, i);
                finish();
                break;
            case R.id.btnPositivo:
                switch (tipoDeMuestraDeContacto){
                    case 1:
                        agregarContacto();
                        break;
                    case 3:
                        mostrarDialogoConfirmacion();
                        break;
                }

                break;

        }
    }

    private void mostrarDialogoConfirmacion(){
        final AlertDialog.Builder dialogoBuscar = new AlertDialog.Builder(this);
        dialogoBuscar.setTitle("Editar Contacto");
        dialogoBuscar.setMessage(String.format("¿Está seguro de que desea editar el contacto \"%s\"?", nombreContacto));
        dialogoBuscar.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogoBuscar, int id) {
                editarContacto();
                dialogoBuscar.dismiss();
            }
        });

        dialogoBuscar.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogoBuscar, int id) {
                i = new Intent();//"com.jordan.ListarActivity"
                setResult(RESULT_CANCELED, i);
                finish();
                dialogoBuscar.dismiss();
            }
        });
        dialogoBuscar.show();

    }

    private void editarContacto(){
        try {
            int idContactoNew = Integer.valueOf(etIDContacto.getText().toString());
            String nombre = etNombre.getText().toString();
            String apellido1 = etApellido1.getText().toString();
            String apellido2 = etApellido2.getText().toString();
            int celular = Integer.valueOf(etCelular.getText().toString());
            int casa = Integer.valueOf(etCasa.getText().toString());
            String direccion = etDireccion.getText().toString();

            database.open();

            if (database.editarContacto(iDContacto, idContactoNew, nombre, apellido1, apellido2, celular, casa, direccion)) {
                i = new Intent();//"com.jordan.ListarActivity"
                i.putExtra(getString(R.string.agregarResultadoKey), String.format("Se ha actualizado el contacto \"%s\".", nombre));
                setResult(RESULT_OK, i);
                finish();
                //Toast.makeText(this, String.format("Se ha agregado el contacto \"%s\".", nombre), Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(this, "Error al actualizar el contacto. El número de identificación o el nombre del contacto ya se encontraba registrado", Toast.LENGTH_LONG).show();
            database.close();
        }catch (Exception e){
            Log.e("Exception: ", e.getMessage());
            Toast.makeText(this, "Error al agregar el contacto. No se permite ingresar letras o signos en los campos numéricos.", Toast.LENGTH_LONG).show();
        }

    }

    private void mostrarContacto() {
        database.open();
        Cursor cursor = database.getContacto(iDContacto);

        if(cursor.moveToFirst() && cursor.getCount() == 1){
            etIDContacto.setText(String.valueOf(cursor.getInt(0)));
            etNombre.setText(cursor.getString(1));
            nombreContacto = cursor.getString(1);
            etApellido1.setText(cursor.getString(2));
            etApellido2.setText(cursor.getString(3));
            etCelular.setText(String.valueOf(cursor.getInt(4)));
            etCasa.setText(String.valueOf(cursor.getInt(5)));
            etDireccion.setText(cursor.getString(6));
        }

        database.close();
    }

    private void agregarContacto() {
        try {
            int id = Integer.valueOf(etIDContacto.getText().toString());
            String nombre = etNombre.getText().toString();
            String apellido1 = etApellido1.getText().toString();
            String apellido2 = etApellido2.getText().toString();
            int celular = Integer.valueOf(etCelular.getText().toString());
            int casa = Integer.valueOf(etCasa.getText().toString());
            String direccion = etDireccion.getText().toString();

            database.open();
            if (database.insertarContacto(id, nombre, apellido1, apellido2, celular, casa, direccion)) {
                i = new Intent();//"com.jordan.MainActivity"
                i.putExtra(getString(R.string.agregarResultadoKey), String.format("Se ha agregado el contacto \"%s\".", nombre));
                setResult(RESULT_OK, i);
                finish();
                //Toast.makeText(this, String.format("Se ha agregado el contacto \"%s\".", nombre), Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(this, "Error al agregar el contacto. El número de identificación o el nombre del contacto ya se encontraba registrado", Toast.LENGTH_LONG).show();

            database.close();
        }catch (Exception e){
            Log.e("Exception: ", e.getMessage());
            Toast.makeText(this, "Error al agregar el contacto. No se permite ingresar letras o signos en los campos numéricos.", Toast.LENGTH_LONG).show();
        }

    }

    private void deshabilitarYOcultar(){
        /*etIDContacto.setEnabled(false);
        etNombre.setEnabled(false);
        etApellido1.setEnabled(false);
        etApellido2.setEnabled(false);
        etCelular.setEnabled(false);
        etCasa.setEnabled(false);
        etDireccion.setEnabled(false);*/

        etIDContacto.setFocusable(false);
        etNombre.setFocusable(false);
        etApellido1.setFocusable(false);
        etApellido2.setFocusable(false);
        etCelular.setFocusable(false);
        etCasa.setFocusable(false);
        etDireccion.setFocusable(false);

        btnCancelar.setVisibility(View.INVISIBLE);
        btnPositivo.setVisibility(View.INVISIBLE);

    }

}
