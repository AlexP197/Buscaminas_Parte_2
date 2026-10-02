/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.buscaminas;

/**
 *
 * @author perea
 */
import javax.swing.*;
import java.awt.*;
import java.util.Random;

/**
 * Buscaminas
 */
public class Buscaminas extends JFrame {

    private static final String[] DIFICULTADES = {"Fácil", "Medio", "Difícil"};
    private static final double[] PORCENTAJE_MINAS = {0.10, 0.15, 0.20};
 
    private int filas = 10;
    private int columnas = 10;
    private int minas;
 
    private JButton[][] botones;
    private boolean[][] esMina;
    private boolean[][] descubierta;
    private int[][] minasAlrededor;
 
    private boolean juegoTerminado;
    private int casillasDescubiertas;
 
    private JLabel lblMinas;
    private JLabel lblResultado;
    private JPanel panelTablero;
    private JComboBox<String> comboDificultad;
    private JFrame ventanaAyuda;
 
    public Buscaminas() {
        setTitle("Buscaminas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        setResizable(false);
 
        setJMenuBar(crearBarraMenus());
 
        GridBagConstraints gbcFrame = new GridBagConstraints();
        gbcFrame.gridx = 0;
        gbcFrame.fill = GridBagConstraints.BOTH;
        gbcFrame.weightx = 1.0;
 
        gbcFrame.gridy = 0;
        gbcFrame.weighty = 0.0;
        add(crearPanelSuperior(), gbcFrame);
 
        panelTablero = new JPanel();
        gbcFrame.gridy = 1;
        gbcFrame.weighty = 1.0;
        add(panelTablero, gbcFrame);
 
        gbcFrame.gridy = 2;
        gbcFrame.weighty = 0.0;
        add(crearPanelInferior(), gbcFrame);
 
        nuevaPartida();
 
        setLocationRelativeTo(null);
        setVisible(true);
    }
 
    private JMenuBar crearBarraMenus() {
        JMenuBar barra = new JMenuBar();
 
        JMenu menuPartida = new JMenu("Partida");
        JMenuItem itemNueva = new JMenuItem("Nueva partida");
        itemNueva.setAccelerator(KeyStroke.getKeyStroke("control N"));
        itemNueva.addActionListener(e -> nuevaPartida());
        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(e -> System.exit(0));
        menuPartida.add(itemNueva);
        menuPartida.addSeparator();
        menuPartida.add(itemSalir);
 
        JMenu menuTamano = new JMenu("Tamaño");
        ButtonGroup grupo = new ButtonGroup();
        menuTamano.add(crearItemTamano("Pequeño (8 x 8)", 8, 8, grupo, false));
        menuTamano.add(crearItemTamano("Mediano (10 x 10)", 10, 10, grupo, true));
        menuTamano.add(crearItemTamano("Grande (14 x 14)", 14, 14, grupo, false));
        menuTamano.add(crearItemTamano("Ancho (10 x 16)", 10, 16, grupo, false));
 
        JMenu menuAyuda = new JMenu("Ayuda");
        JMenuItem itemComoJugar = new JMenuItem("Cómo jugar...");
        itemComoJugar.addActionListener(e -> abrirVentanaAyuda());
        menuAyuda.add(itemComoJugar);
 
        barra.add(menuPartida);
        barra.add(menuTamano);
        barra.add(menuAyuda);
        return barra;
    }
 
    private JRadioButtonMenuItem crearItemTamano(String texto, int nFilas, int nColumnas,
                                                 ButtonGroup grupo, boolean seleccionado) {
        JRadioButtonMenuItem item = new JRadioButtonMenuItem(texto, seleccionado);
        item.addActionListener(e -> {
            filas = nFilas;
            columnas = nColumnas;
            nuevaPartida();
        });
        grupo.add(item);
        return item;
    }
 
    private void abrirVentanaAyuda() {
        if (ventanaAyuda != null && ventanaAyuda.isDisplayable()) {
            ventanaAyuda.toFront();
            return;
        }
 
        ventanaAyuda = new JFrame("Cómo jugar");
        ventanaAyuda.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventanaAyuda.setLayout(new BorderLayout(10, 10));
 
        JTextArea texto = new JTextArea(
                "OBJETIVO\n"
                + "Descubre todas las casillas que no tienen mina.\n\n"
                + "REGLAS\n"
                + "- Pulsa una casilla para descubrirla.\n"
                + "- El número indica las minas en las 8 casillas vecinas.\n"
                + "- Si no hay minas alrededor, se descubren las vecinas.\n"
                + "- Si pulsas una mina, pierdes.\n\n"
                + "OPCIONES\n"
                + "- Menú Tamaño: cambia las dimensiones del tablero.\n"
                + "- Dificultad: Fácil 10%, Medio 15%, Difícil 20% de minas.");
        texto.setEditable(false);
        texto.setFont(new Font("Arial", Font.PLAIN, 14));
        texto.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        texto.setBackground(ventanaAyuda.getBackground());
 
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> ventanaAyuda.dispose());
        JPanel panelBoton = new JPanel();
        panelBoton.add(btnCerrar);
 
        ventanaAyuda.add(texto, BorderLayout.CENTER);
        ventanaAyuda.add(panelBoton, BorderLayout.SOUTH);
        ventanaAyuda.pack();
        ventanaAyuda.setLocationRelativeTo(this);
        ventanaAyuda.setVisible(true);
    }
 
    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(""));
 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
 
        JLabel titulo = new JLabel("BUSCAMINAS");
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titulo, gbc);
 
        lblMinas = new JLabel("Minas: 0");
        lblMinas.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        panel.add(lblMinas, gbc);
 
        comboDificultad = new JComboBox<>(DIFICULTADES);
        comboDificultad.setSelectedIndex(1);
        comboDificultad.addActionListener(e -> nuevaPartida());
        gbc.gridx = 1;
        panel.add(comboDificultad, gbc);
 
        return panel;
    }
 
    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
 
        JButton btnNuevaPartida = new JButton("Nueva partida");
        btnNuevaPartida.addActionListener(e -> nuevaPartida());
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(btnNuevaPartida, gbc);
 
        lblResultado = new JLabel("Buena suerte");
        lblResultado.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 1;
        panel.add(lblResultado, gbc);
 
        return panel;
    }
 
    private void construirTablero() {
        panelTablero.removeAll();
        panelTablero.setLayout(new GridBagLayout());
 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
 
        for (int fila = 0; fila < filas; fila++) {
            for (int col = 0; col < columnas; col++) {
                JButton boton = new JButton();
                boton.setPreferredSize(new Dimension(42, 42));
                boton.setMargin(new Insets(0, 0, 0, 0));
                boton.setFont(new Font("Arial", Font.BOLD, 14));
 
                final int f = fila;
                final int c = col;
                boton.addActionListener(e -> descubrirCasilla(f, c));
 
                botones[fila][col] = boton;
 
                gbc.gridx = col;
                gbc.gridy = fila;
                panelTablero.add(boton, gbc);
            }
        }
 
        panelTablero.revalidate();
        panelTablero.repaint();
    }
 
    private int calcularNumeroMinas() {
        int total = filas * columnas;
        double porcentaje = PORCENTAJE_MINAS[comboDificultad.getSelectedIndex()];
        return Math.max(1, (int) Math.round(total * porcentaje));
    }
 
    private void nuevaPartida() {
        minas = calcularNumeroMinas();
 
        botones = new JButton[filas][columnas];
        esMina = new boolean[filas][columnas];
        descubierta = new boolean[filas][columnas];
        minasAlrededor = new int[filas][columnas];
 
        juegoTerminado = false;
        casillasDescubiertas = 0;
 
        construirTablero();
        colocarMinas();
        calcularMinasAlrededor();
 
        lblMinas.setText("Minas: " + minas);
        lblResultado.setText("Buena suerte");
        lblResultado.setForeground(Color.BLACK);
 
        pack();
    }
 
    private void colocarMinas() {
        Random random = new Random();
        int colocadas = 0;
        while (colocadas < minas) {
            int fila = random.nextInt(filas);
            int col = random.nextInt(columnas);
            if (!esMina[fila][col]) {
                esMina[fila][col] = true;
                colocadas++;
            }
        }
    }
 
    private void calcularMinasAlrededor() {
        for (int fila = 0; fila < filas; fila++) {
            for (int col = 0; col < columnas; col++) {
                if (esMina[fila][col]) {
                    continue;
                }
                int contador = 0;
                for (int df = -1; df <= 1; df++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        int nf = fila + df;
                        int nc = col + dc;
                        if (nf >= 0 && nf < filas && nc >= 0 && nc < columnas && esMina[nf][nc]) {
                            contador++;
                        }
                    }
                }
                minasAlrededor[fila][col] = contador;
            }
        }
    }
 
    private void descubrirCasilla(int fila, int col) {
        if (juegoTerminado || descubierta[fila][col]) {
            return;
        }
 
        if (esMina[fila][col]) {
            perderPartida(fila, col);
            return;
        }
 
        revelarEnCadena(fila, col);
 
        if (casillasDescubiertas == filas * columnas - minas) {
            ganarPartida();
        }
    }
 
    // Recursivo: si no hay minas alrededor, se llama con las 8 vecinas
    private void revelarEnCadena(int fila, int col) {
        if (fila < 0 || fila >= filas || col < 0 || col >= columnas) {
            return;
        }
        if (descubierta[fila][col] || esMina[fila][col]) {
            return;
        }
 
        descubierta[fila][col] = true;
        casillasDescubiertas++;
 
        JButton boton = botones[fila][col];
        boton.setEnabled(false);
        boton.setBackground(Color.LIGHT_GRAY);
        boton.setOpaque(true);
        boton.setBorderPainted(true);
 
        int numMinas = minasAlrededor[fila][col];
        if (numMinas > 0) {
            boton.setText(String.valueOf(numMinas));
            boton.setForeground(colorParaNumero(numMinas));
        } else {
            boton.setText("");
            for (int df = -1; df <= 1; df++) {
                for (int dc = -1; dc <= 1; dc++) {
                    revelarEnCadena(fila + df, col + dc);
                }
            }
        }
    }
 
    private Color colorParaNumero(int numero) {
        switch (numero) {
            case 1: return Color.BLUE;
            case 2: return new Color(0, 128, 0);
            case 3: return Color.RED;
            case 4: return new Color(0, 0, 128);
            default: return Color.BLACK;
        }
    }
 
    private void perderPartida(int filaClic, int colClic) {
        juegoTerminado = true;
        for (int fila = 0; fila < filas; fila++) {
            for (int col = 0; col < columnas; col++) {
                JButton boton = botones[fila][col];
                if (esMina[fila][col]) {
                    boton.setText("*");
                    boton.setBackground(fila == filaClic && col == colClic ? Color.RED : Color.ORANGE);
                    boton.setOpaque(true);
                }
                boton.setEnabled(false);
            }
        }
        lblResultado.setText("¡Has perdido!");
        lblResultado.setForeground(Color.RED);
    }
 
    private void ganarPartida() {
        juegoTerminado = true;
        for (int fila = 0; fila < filas; fila++) {
            for (int col = 0; col < columnas; col++) {
                if (esMina[fila][col]) {
                    JButton boton = botones[fila][col];
                    boton.setText("F");
                    boton.setBackground(Color.GREEN);
                    boton.setOpaque(true);
                }
                botones[fila][col].setEnabled(false);
            }
        }
        lblResultado.setText("¡Has ganado!");
        lblResultado.setForeground(new Color(0, 128, 0));
    }
 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Buscaminas::new);
    }
}