package com.example.restaurante;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText etValorConta, etCouvert, etTaxaServico, etNumeroPessoas;
    private TextView tvResultado;
    private Button btnCalcular, btnSair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Barra
        getWindow().setStatusBarColor(Color.parseColor("#140A07"));
        getWindow().setNavigationBarColor(Color.parseColor("#140A07"));

        // Inicializando os campos de entrada e o botão
        etValorConta = findViewById(R.id.etValorConta);
        etCouvert = findViewById(R.id.etCouvert);
        etTaxaServico = findViewById(R.id.etTaxaServico);
        etNumeroPessoas = findViewById(R.id.etNumeroPessoas);
        tvResultado = findViewById(R.id.tvResultado);
        btnCalcular = findViewById(R.id.btnCalcular);
        btnSair = findViewById(R.id.btnSair);

        // Adicionando formatação para campos monetários
        formatarEditText(etValorConta);
        formatarEditText(etCouvert);
        formatarTaxaServico(etTaxaServico);  // Formatação para campo de taxa

        // Adicionando evento de clique ao botão de calcular
        btnCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calcularConta();
            }
        });

        // Adicionando evento de clique ao botão sair
        btnSair.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

    }

    // Função para adicionar um TextWatcher que formata como valor monetário
    private void formatarEditText(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    editText.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[R$,.]", "").replaceAll("\\s", "");

                    double parsed;
                    try {
                        parsed = Double.parseDouble(cleanString) / 100;
                    } catch (NumberFormatException e) {
                        parsed = 0.00;
                    }

                    NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
                    String formatted = formatter.format(parsed);

                    current = formatted;
                    editText.setText(formatted);
                    editText.setSelection(formatted.length());

                    editText.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Função para formatar o campo de taxa de serviço
    private void formatarTaxaServico(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    editText.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[,.]", "").replaceAll("\\s", "");

                    double parsed;
                    try {
                        parsed = Double.parseDouble(cleanString) / 100;
                    } catch (NumberFormatException e) {
                        parsed = 0.00;
                    }

                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                    String formatted = decimalFormat.format(parsed);

                    current = formatted;
                    editText.setText(formatted);
                    editText.setSelection(formatted.length());

                    editText.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void calcularConta() {
        try {
            // Obtendo valores dos campos e removendo a formatação
            double valorConta = parseDoubleFromText(etValorConta.getText().toString());
            double couvertArtistico = parseDoubleFromText(etCouvert.getText().toString());
            double taxaServico = parseDoubleFromText(etTaxaServico.getText().toString()) / 100;

            // Verificando o número de pessoas para evitar divisão por zero
            int numeroPessoas = Integer.parseInt(etNumeroPessoas.getText().toString());
            if (numeroPessoas <= 0) {
                tvResultado.setText("O número de pessoas deve ser maior que zero.");
                return;
            }

            // Calculando o total
            double totalTaxaServico = valorConta * taxaServico;
            double totalConta = valorConta + couvertArtistico + totalTaxaServico;

            // Calculando o valor por pessoa (rateio)
            double valorPorPessoa = totalConta / numeroPessoas;

            // Formatando o resultado para exibição
            DecimalFormat df = new DecimalFormat("#,##0.00");
            String resultado = "Total a pagar: R$ " + df.format(totalConta) + "\n" +
                    "Valor por pessoa: R$ " + df.format(valorPorPessoa);

            // Exibindo o resultado
            tvResultado.setText(resultado);

            // Ocultando o teclado
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            // Limpando os campos de entrada
            etValorConta.setText("");
            etCouvert.setText("");
            etTaxaServico.setText("");
            etNumeroPessoas.setText("");

        } catch (Exception e) {
            tvResultado.setText("Erro ao calcular. Verifique os valores.");
            e.printStackTrace();
        }
    }

    // Função para converter string formatada para double
    private double parseDoubleFromText(String value) {
        try {
            String cleanString = value.replaceAll("[R$,]", "").replaceAll("\\s", "");
            return cleanString.isEmpty() ? 0.0 : Double.parseDouble(cleanString) / 100;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
