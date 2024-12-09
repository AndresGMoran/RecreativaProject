package com.sak.myrecreativa.ui.fragments.sudoku;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sak.myrecreativa.R;
import com.sak.myrecreativa.models.games.sudoku.SudokuGame;

public class SudokuGameFragment extends Fragment {

    private SudokuGame sudokuGame;
    private TableLayout tableLayout;
    private EditText numberInput;
    private Button ingresarButton;
    private Button finishButton;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private String mode;
    private int boardSize;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Bundle args = getArguments();
        if (args != null && args.containsKey("MODE")) {
            mode = args.getString("MODE");
        } else {
            mode = "medium"; // Default mode
        }

        // Configura el tamaño del tablero basado en el modo
        switch (mode.toLowerCase()) {
            case "easy":
                boardSize = 6; // Tamaño 6x6 para fácil
                break;
            case "medium":
                boardSize = 9; // Tamaño 9x9 para medio
                break;
            case "hard":
                boardSize = 12; // Tamaño 12x12 para difícil
                break;
            default:
                boardSize = 9; // Por defecto, 9x9
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sudoku_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tableLayout = view.findViewById(R.id.sudokuTable);
        numberInput = view.findViewById(R.id.numberInput);
        ingresarButton = view.findViewById(R.id.ingresarButton);
        finishButton = view.findViewById(R.id.finishButton);

        // Inicializar el juego con el tamaño del tablero determinado
        sudokuGame = new SudokuGame(boardSize);

        createGameBoard();

        ingresarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = numberInput.getText().toString();
                if (input.isEmpty()) {
                    Toast.makeText(getContext(), "Por favor, ingresa un número", Toast.LENGTH_SHORT).show();
                    return;
                }

                int number;
                try {
                    number = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Número inválido", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (number < 1 || number > boardSize) {
                    Toast.makeText(getContext(), "El número debe estar entre 1 y " + boardSize, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedRow == -1 || selectedCol == -1) {
                    Toast.makeText(getContext(), "Selecciona una celda primero", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (sudokuGame.makeMove(selectedRow, selectedCol, number)) {
                    updateCell(selectedRow, selectedCol, number);
                    if (sudokuGame.isSolved()) {
                        Toast.makeText(getContext(), "¡Felicidades! Has completado el Sudoku", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Movimiento no permitido", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Configurar el botón para finalizar el juego
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sudokuGame.isSolved()) {
                    Toast.makeText(getContext(), "¡Juego completado exitosamente!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "El juego no está completo. ¡Sigue intentándolo!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void handleNumberInput() {
        String input = numberInput.getText().toString();
        if (input.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, ingresa un número", Toast.LENGTH_SHORT).show();
            return;
        }

        int number;
        try {
            number = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Número inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (number < 1 || number > boardSize) {
            Toast.makeText(getContext(), "El número debe estar entre 1 y " + boardSize, Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedRow == -1 || selectedCol == -1) {
            Toast.makeText(getContext(), "Selecciona una celda primero", Toast.LENGTH_SHORT).show();
            return;
        }

        if (sudokuGame.makeMove(selectedRow, selectedCol, number)) {
            updateCell(selectedRow, selectedCol, number);
            if (sudokuGame.isSolved()) {
                Toast.makeText(getContext(), "¡Felicidades! Has completado el Sudoku", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getContext(), "Movimiento no permitido", Toast.LENGTH_SHORT).show();
        }
    }

    private void createGameBoard() {
        tableLayout.removeAllViews(); // Limpia cualquier tablero previo

        int[][] board = sudokuGame.getBoard();

        for (int i = 0; i < boardSize; i++) {
            TableRow row = new TableRow(getContext());
            row.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            ));

            for (int j = 0; j < boardSize; j++) {
                EditText cell = new EditText(getContext());
                TableRow.LayoutParams params = new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(2, 2, 2, 2);
                cell.setLayoutParams(params);

                cell.setGravity(View.TEXT_ALIGNMENT_CENTER);
                cell.setTextSize(16);

                if (board[i][j] != 0) {
                    cell.setText(String.valueOf(board[i][j]));
                    cell.setFocusable(false);
                } else {
                    cell.setHint("-");
                }

                int finalI = i, finalJ = j;
                cell.setOnClickListener(v -> {
                    selectedRow = finalI;
                    selectedCol = finalJ;
                    Toast.makeText(getContext(), "Celda seleccionada: (" + (finalI + 1) + ", " + (finalJ + 1) + ")", Toast.LENGTH_SHORT).show();
                });

                row.addView(cell);
            }
            tableLayout.addView(row);
        }
    }

    private void updateCell(int row, int col, int number) {
        TableRow tableRow = (TableRow) tableLayout.getChildAt(row);
        if (tableRow != null) {
            EditText cell = (EditText) tableRow.getChildAt(col);
            if (cell != null) {
                cell.setText(String.valueOf(number));
            }
        }
    }
}