package model.vento;

import java.util.Arrays;

public class MatrizVento {
  private final double coef;
  private final double mult_base;

  private final double decai;
  private static MatrizVento instance;
  private Enum<DirecoesVento> direcao;
  private double[][] matrizVento;

  public MatrizVento(double coef, double mult_base, double decai, Enum<DirecoesVento> direcao) {
    this.coef = coef;
    this.mult_base = mult_base;
    this.decai = decai;

    this.matrizVento = new double[7][7];

    setDirecao(direcao);
  }

  /**
   * @param coef Coeficiente base do vento
   * @param mult_base Multiplicador base do vento
   * @param decai Quão maior mais direcional éo vento
   * @return Singleton da classe MatrizVento
   */
  public static MatrizVento getInstance(
      double coef, double mult_base, double decai, Enum<DirecoesVento> direcao) {
    if (instance == null) instance = new MatrizVento(coef, mult_base, decai, direcao);
    if (!instance.direcao.equals(direcao))
      instance = new MatrizVento(coef, mult_base, decai, direcao);
    return instance;
  }
  public void setDirecao(Enum<DirecoesVento> novaDirecao) {
    matrizVento[1][1] = 0;
    matrizVento[0][0] = coef * (mult_base - (decai * 1));
    matrizVento[0][1] = coef * (mult_base - (decai * 0));
    matrizVento[0][2] = coef * (mult_base - (decai * 1));
    matrizVento[1][0] = coef * (mult_base - (decai * 2));
    matrizVento[1][2] = coef * (mult_base - (decai * 2));
    matrizVento[2][0] = coef * (mult_base - (decai * 3));
    matrizVento[2][1] = coef * (mult_base - (decai * 4));
    matrizVento[2][2] = coef * (mult_base - (decai * 3));
    // ------------------------ Matriz faísca ------------------------



    this.direcao = novaDirecao;

    if (direcao.equals(DirecoesVento.S)) {

    } else if (direcao.equals(DirecoesVento.SO)) {
      rotate(1);
    } else if (direcao.equals(DirecoesVento.O)) {
      rotate(2);
    } else if (direcao.equals(DirecoesVento.NO)) {
      rotate(3);
    } else if (direcao.equals(DirecoesVento.N)) {
      rotate(4);
    } else if (direcao.equals(DirecoesVento.NE)) {
      rotate(5);
    } else if (direcao.equals(DirecoesVento.E)) {
      rotate(6);
    } else if (direcao.equals(DirecoesVento.SE)) {
      rotate(7);
    }
  }

  public double[][] getMatrizVento() {
    return matrizVento;
  }

  private void rotate() {
    int size = 7;
    int row = 0, col = 0;
    int m = size, n = size;
    double prev, curr;

    /*
    row - Starting row index
    m - ending row index
    col - starting column index
    n - ending column index
    i - iterator
    */
    while (row < m && col < n) {

      if (row + 1 == m || col + 1 == n) break;

      // Store the first element of next row, this
      // element will replace first element of current
      // row
      prev = matrizVento[row + 1][col];

      /* Move elements of first row from the remaining rows */
      for (int i = col; i < n; i++) {
        curr = matrizVento[row][i];
        matrizVento[row][i] = prev;
        prev = curr;
      }
      row++;

      /* Move elements of last column from the remaining columns */
      for (int i = row; i < m; i++) {
        curr = matrizVento[i][n - 1];
        matrizVento[i][n - 1] = prev;
        prev = curr;
      }
      n--;

      /* Move elements of last row from the remaining rows */
      if (row < m) {
        for (int i = n - 1; i >= col; i--) {
          curr = matrizVento[m - 1][i];
          matrizVento[m - 1][i] = prev;
          prev = curr;
        }
      }
      m--;

      /* Move elements of first column from the remaining rows */
      if (col < n) {
        for (int i = m - 1; i >= row; i--) {
          curr = matrizVento[i][col];
          matrizVento[i][col] = prev;
          prev = curr;
        }
      }
      col++;
    }

    // Print rotated matrix
  }

  private void rotate(int times) {
    if (times <= 0) throw new IllegalArgumentException("Numero de rotações invalido");
    for (int i = 0; i < times; i++) {
      rotate();
    }
  }
  public static void main(String[] args) {
    MatrizVento matrizVento = new MatrizVento(1, 1, 0.2, DirecoesVento.N);
    double[][] matriz = matrizVento.getMatrizVento();
    for (int i = 0; i < matriz.length; i++) {
      System.out.println(Arrays.toString(matriz[i]));
    }
  }
}
