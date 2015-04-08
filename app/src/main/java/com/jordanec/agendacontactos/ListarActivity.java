package com.jordanec.agendacontactos;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class ListarActivity extends ActionBarActivity {
    Database database;
    String contacto = "";
    ListView listView;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar);
        database = new Database(this);
        contacto = getIntent().getStringExtra(getString(R.string.listarKey));
        setListView(contacto);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            String r = data.getStringExtra(getString(R.string.agregarResultadoKey));
            Toast.makeText(this, r, Toast.LENGTH_LONG).show();
        }

    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_listar, menu);
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
    }*/

    private void setListView(String contacto){
        Cursor cursor;
        Map<Integer, List<String>> mapContactos;
        CustomListAdapter customListAdapter;
        List<String> contactos;
        listView = (ListView) findViewById(R.id.lvListarContactos);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int iDContacto = Integer.valueOf(((ArrayList) parent.getAdapter().getItem(position)).get(0).toString());
                i = new Intent("com.jordanec.MostrarContactoActivity");
                i.putExtra(getString(R.string.tipoDeMuestraDeContactoKey), 2);              //sin botones
                i.putExtra(getString(R.string.iDContactoKey), iDContacto);
                startActivityForResult(i, 1);


            }
        });


        database.open();

        if(contacto.isEmpty())
            cursor = database.getContactos(true);
        else
            cursor = database.getContacto(contacto);

        if(cursor.moveToFirst()){
            mapContactos = new LinkedHashMap<>();
            int i =0;
            do{
                contactos = new ArrayList<>();
                contactos.add(String.valueOf(cursor.getInt(0)));    //iDContacto
                contactos.add(cursor.getString(1));                 //nombre
                contactos.add(cursor.getString(2));                 //primerApellido
                mapContactos.put(i, contactos);
                i++;
            }while(cursor.moveToNext());
            customListAdapter = new CustomListAdapter(mapContactos);
            listView.setAdapter(customListAdapter);
        }
        else {
            Map<Integer, List<String>> map = new LinkedHashMap<>();
            customListAdapter = new CustomListAdapter(map);
            listView.setAdapter(customListAdapter);
            Toast.makeText(this, "No se encontraron contactos.", Toast.LENGTH_LONG).show();
            //TextView textView = new TextView(this);
            //textView.setText(getString(R.string.mensajeSinContactos));
            //listView.addView(textView);
        }
        database.close();
    }

    class CustomListAdapter extends BaseAdapter implements View.OnClickListener{
        private Map<Integer, List<String>> mapContactos;

        public CustomListAdapter(Map<Integer, List<String>> mapContactos){
            this.mapContactos = mapContactos;
        }


        @Override
        public int getCount() {
            return mapContactos.size();
        }

        @Override
        public Object getItem(int position) {
            return mapContactos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null)
                convertView = getLayoutInflater().inflate(R.layout.contacto_item, parent, false);


            TextView tv1 = (TextView) convertView.findViewById(R.id.tvIDContacto);
            tv1.setText(mapContactos.get(position).get(0));
            TextView tv2 = (TextView) convertView.findViewById(R.id.tvNombreApellido1);
            tv2.setText(mapContactos.get(position).get(1) + " " + mapContactos.get(position).get(2));

            Button btnEditar = (Button) convertView.findViewById(R.id.btnEditar);
            Button btnEliminar = (Button) convertView.findViewById(R.id.btnEliminar);
            btnEditar.setOnClickListener(this);
            btnEliminar.setOnClickListener(this);

            return convertView;
        }

        @Override
        public void onClick(View v) {
            int iDContacto = Integer.valueOf((((TextView)((LinearLayout)v.getParent()).findViewById(R.id.tvIDContacto)).getText().toString()));

            switch (v.getId()){
                case R.id.btnEditar:

                    i = new Intent("com.jordanec.MostrarContactoActivity");
                    i.putExtra(getString(R.string.tipoDeMuestraDeContactoKey), 3);              //editar
                    i.putExtra(getString(R.string.iDContactoKey), iDContacto);   //iDContacto
                    startActivityForResult(i, 1);
                    break;
                case R.id.btnEliminar:
                    database.open();
                    if(database.borrarContacto(iDContacto)) {
                        Toast.makeText(ListarActivity.this, String.format("El contacto con el identificador \"%d\" se borró correctamente.", iDContacto), Toast.LENGTH_LONG).show();
                        //LinearLayout ll = (LinearLayout) v.getParent();
                        //((CustomListAdapter)((ListView)ll.getParent()).getAdapter()).// removeView(ll);
                        setListView("");
                    }
                    database.close();
                    //ListarActivity.this.recreate();
                    break;
            }
        }
    }


}
