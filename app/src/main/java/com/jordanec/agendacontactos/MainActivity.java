package com.jordanec.agendacontactos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    Button btnListar;
    Button btnBuscar;
    Button btnAgregar;
    Button btnEliminar;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle b = msg.getData();
                String s = b.getString(getString(R.string.toastKey));
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
            }
        };
        setButtons();

    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    private void setButtons() {
        btnListar = (Button) findViewById(R.id.btnListar);
        btnBuscar = (Button) findViewById(R.id.btnBuscar);
        btnAgregar = (Button) findViewById(R.id.btnAgregar);
        btnEliminar = (Button) findViewById(R.id.btnEliminar);

        btnListar.setOnClickListener(this);
        btnBuscar.setOnClickListener(this);
        btnAgregar.setOnClickListener(this);
        btnEliminar.setOnClickListener(this);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            String r = data.getStringExtra(getString(R.string.agregarResultadoKey));
            Toast.makeText(this, r, Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.btnListar:
                i = new Intent("com.jordanec.ListarActivity");
                i.putExtra(getString(R.string.listarKey), "");
                startActivity(i);
                break;

            case R.id.btnBuscar:
                mostrarDialogoBuscar();
                break;

            case R.id.btnAgregar:
                i = new Intent("com.jordanec.MostrarContactoActivity");
                i.putExtra(getString(R.string.tipoDeMuestraDeContactoKey), 1);              //con boton agregar y  cancelar
                startActivityForResult(i, 1);
                break;

            case R.id.btnEliminar:
                mostrarDialogoEliminar();
                break;

        }

    }

    private void mostrarDialogoBuscar() {
        final AlertDialog.Builder dialogoBuscar = new AlertDialog.Builder(this);
        final EditText entrada = new EditText(this);
        entrada.setText("");
        dialogoBuscar.setTitle("Buscar Contacto");
        dialogoBuscar.setView(entrada);
        dialogoBuscar.setMessage("Ingrese el nombre del contacto:");
        //dialogoBuscar.setCancelable(false);
        dialogoBuscar.setPositiveButton("Buscar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogoBuscar, int id) {
                String s = entrada.getText().toString();
                if (s.length() > 1) {
                    Intent i = new Intent("com.jordanec.ListarActivity");
                    i.putExtra(getString(R.string.listarKey), s);
                    startActivity(i);
                    dialogoBuscar.dismiss();
                }
                else{
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString(getString(R.string.toastKey), "Debe ingresar como mínimo 2 caracteres para realizar la búsqueda.");
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }

            }
        });
        dialogoBuscar.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogoBuscar, int id) {
                dialogoBuscar.dismiss();
            }
        });
        dialogoBuscar.show();
    }

    private void mostrarDialogoEliminar(){
        final AlertDialog.Builder dialogoBuscar = new AlertDialog.Builder(this);
        final EditText entrada = new EditText(this);
        final Database database = new Database(this);
        entrada.setText("");
        dialogoBuscar.setTitle("Eliminar Contacto");
        dialogoBuscar.setView(entrada);
        dialogoBuscar.setMessage("Ingrese el número de identificación del contacto que desea eliminar:");
        dialogoBuscar.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogoBuscar, int id) {
                String s;
                try {
                    int i = Integer.valueOf(entrada.getText().toString());
                    database.open();
                    if (database.borrarContacto(i))
                        s = String.format("El contacto con el identificador \"%d\" se borró correctamente.", i);
                    else
                        s = String.format("No existe un contacto con el identificador \"%d\".", i);

                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString(getString(R.string.toastKey), s);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                    database.close();
                    dialogoBuscar.dismiss();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Sólo se permiten números en el identificador del contacto.", Toast.LENGTH_LONG).show();

                }

            }
        });

        dialogoBuscar.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogoBuscar, int id) {
                dialogoBuscar.dismiss();
            }
        });
        dialogoBuscar.show();

    }

}
