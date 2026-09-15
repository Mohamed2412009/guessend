package com.example.spinnerguess;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerDifficulty;
    private EditText etGuessInput, etName;
    private TextView tvFeedback, tvAttempts, tvTimer, tvScore;
    private Button btnSubmitGuess, btnNewGame;

    private GuessGame game;
    private CountDownTimer timer;
    private int remainingSeconds;
    private boolean isTimerRunning = false;
    private DBHelper db;
    private ModelUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerDifficulty = findViewById(R.id.spinnerDifficulty);
        etGuessInput = findViewById(R.id.etGuessInput);
        etName = findViewById(R.id.name);
        tvFeedback = findViewById(R.id.tvFeedback);
        tvAttempts = findViewById(R.id.tvAttempts);
        tvTimer = findViewById(R.id.tvTimer);
        tvScore = findViewById(R.id.tvScore);
        btnSubmitGuess = findViewById(R.id.btnSubmitGuess);
        btnNewGame = findViewById(R.id.btnNewGame);

        ArrayAdapter<GuessGame.Difficulty> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                GuessGame.Difficulty.values()
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(adapter);

        game = new GuessGame(GuessGame.Difficulty.EASY);
        db = new DBHelper(this);

        showLoginDialog();

        etGuessInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isTimerRunning && s.length() > 0 && !game.isGameOver()) {
                    GuessGame.Difficulty selectedDifficulty = (GuessGame.Difficulty) spinnerDifficulty.getSelectedItem();
                    startTimer(selectedDifficulty.getTimeSeconds());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewGame();
            }
        });

        btnSubmitGuess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleGuess();
            }
        });
    }

    private void showLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_login, null);
        builder.setView(dialogView);
        builder.setCancelable(false);

        EditText etLoginUName = dialogView.findViewById(R.id.etLoginUName);
        EditText etLoginPassword = dialogView.findViewById(R.id.etLoginPassword);
        Button btnLogin = dialogView.findViewById(R.id.btnLogin);
        Button btnGoToRegister = dialogView.findViewById(R.id.btnGoToRegister);

        AlertDialog dialog = builder.create();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uName = etLoginUName.getText().toString().trim();
                String password = etLoginPassword.getText().toString().trim();

                if (uName.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                ArrayList<ModelUser> users = db.selectByUName(uName);
                if (!users.isEmpty()) {
                    ModelUser user = users.get(0);
                    if (user.getPassword().equals(password)) {
                        currentUser = user;
                        Toast.makeText(MainActivity.this, "Welcome " + currentUser.getUserName(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        startNewGame();
                    } else {
                        Toast.makeText(MainActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Username not found, please register", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showRegisterDialog();
            }
        });

        dialog.show();
    }

    private boolean isValidPassword(String password) {
        String passwordRegex = "^[A-Z](?=.*[0-9])(?=.*[a-zA-Z]).{5,}$";
        return password.matches(passwordRegex);
    }

    private void showRegisterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_register, null);
        builder.setView(dialogView);
        builder.setCancelable(false);

        EditText etRegName = dialogView.findViewById(R.id.etRegName);
        EditText etRegUName = dialogView.findViewById(R.id.etRegUName);
        EditText etRegPassword = dialogView.findViewById(R.id.etRegPassword);
        EditText etRegConfirmPassword = dialogView.findViewById(R.id.etRegConfirmPassword);
        Button btnRegister = dialogView.findViewById(R.id.btnRegister);
        Button btnRegisterCancel = dialogView.findViewById(R.id.btnRegisterCancel);

        AlertDialog dialog = builder.create();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etRegName.getText().toString().trim();
                String uName = etRegUName.getText().toString().trim();
                String password = etRegPassword.getText().toString().trim();
                String confirmPassword = etRegConfirmPassword.getText().toString().trim();

                if (name.isEmpty() || uName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isValidPassword(password)) {
                    Toast.makeText(MainActivity.this, "Password must be at least 6 chars, start with an uppercase letter, and contain both letters and numbers!", Toast.LENGTH_LONG).show();
                    etRegPassword.setError("Invalid password format");
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(MainActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                    etRegConfirmPassword.setError("Passwords do not match");
                    return;
                }

                ArrayList<ModelUser> existing = db.selectByUName(uName);
                if (!existing.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Username already exists, choose another", Toast.LENGTH_SHORT).show();
                    return;
                }

                ModelUser newUser = new ModelUser(name, uName, password, 0, 0);
                db.insert(newUser);
                Toast.makeText(MainActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                showLoginDialog();
            }
        });

        btnRegisterCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
    }

    private void updateDBAutomatically() {
        if (currentUser != null) {
            int currentScore = game.getScore();
            if (currentScore > currentUser.getScore()) {
                currentUser.setScore(currentScore);
                db.update(currentUser);
            }
        }
    }

    private void startNewGame() {
        if (timer != null) {
            timer.cancel();
        }

        isTimerRunning = false;
        GuessGame.Difficulty selectedDifficulty = (GuessGame.Difficulty) spinnerDifficulty.getSelectedItem();
        game.resetGame(selectedDifficulty);

        updateUI();
        tvTimer.setText("Time Left: " + selectedDifficulty.getTimeSeconds() + "s");
        tvFeedback.setText("Game started! Type a number to start the timer.");

        etGuessInput.requestFocus();
    }

    private void startTimer(int seconds) {
        isTimerRunning = true;
        remainingSeconds = seconds;
        timer = new CountDownTimer(seconds * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingSeconds = (int) (millisUntilFinished / 1000);
                tvTimer.setText("Time Left: " + remainingSeconds + "s");
            }

            @Override
            public void onFinish() {
                remainingSeconds = 0;
                isTimerRunning = false;
                tvTimer.setText("Time Left: 0s");
                game.timeOut();
                tvFeedback.setText("Time's up! You lost.");
                btnSubmitGuess.setEnabled(false);

                updateDBAutomatically();
                Intent intent = new Intent(MainActivity.this, showsTops.class);
                startActivity(intent);
                finish();
            }
        }.start();
    }

    private void handleGuess() {
        String input = etGuessInput.getText().toString().trim();
        if (input.isEmpty()) {
            Toast.makeText(this, "Please enter a number", Toast.LENGTH_SHORT).show();
            return;
        }

        int userGuess = Integer.parseInt(input);
        String resultMessage = game.makeGuess(userGuess, remainingSeconds);

        tvFeedback.setText(resultMessage);
        etGuessInput.setText("");
        etGuessInput.requestFocus();
        updateUI();

        if (game.isGameOver()) {
            if (timer != null) {
                timer.cancel();
            }
            isTimerRunning = false;
            btnSubmitGuess.setEnabled(false);

            updateDBAutomatically();
            Intent intent = new Intent(MainActivity.this, showsTops.class);
            startActivity(intent);
            finish();
        }
    }

    private void updateUI() {
        tvAttempts.setText("Attempts Left: " + game.getRemainingAttempts());
        tvScore.setText("Score: " + ScoreText());
        btnSubmitGuess.setEnabled(!game.isGameOver());
    }

    private int ScoreText() {
        return game.getScore();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }
}