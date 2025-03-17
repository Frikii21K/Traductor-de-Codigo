import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.util.List;

public class VentanaTraductor extends JFrame {

    private JTextArea txtCodigo;
    private JTable tablaErrores;
    private DefaultTableModel modeloErrores;
    private JButton btnTraducir;
    private JButton btnLimpiar;
    private JButton btnNuevo;
    private JLabel lblMensajeError;
    private JComboBox<String> comboOrigen;
    private JComboBox<String> comboDestino;
    private JFrame ventanaTraduccion; // Ventana para mostrar el código traducido
    private final String PLACEHOLDER = "Copia o pega tu código aquí";

    public VentanaTraductor() {
        setTitle("Traductor de Código");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        initComponentes();
        setVisible(true);
    }

    private void initComponentes() {
        configurarLookAndFeel();
        configurarFuentes();

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblHeader = crearHeader();
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.add(lblHeader, BorderLayout.CENTER);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Área de código fuente 
        txtCodigo = crearTextArea();
        txtCodigo.setText(PLACEHOLDER);
        txtCodigo.setForeground(Color.GRAY);
        txtCodigo.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtCodigo.getText().equals(PLACEHOLDER)) {
                    txtCodigo.setText("");
                    txtCodigo.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (txtCodigo.getText().trim().isEmpty()) {
                    txtCodigo.setText(PLACEHOLDER);
                    txtCodigo.setForeground(Color.GRAY);
                }
            }
        });
        JScrollPane scrollCodigo = new JScrollPane(txtCodigo);
        scrollCodigo.setBorder(BorderFactory.createTitledBorder("Código Fuente"));
        scrollCodigo.setPreferredSize(new Dimension(0, 400));
        mainPanel.add(scrollCodigo, BorderLayout.CENTER);

        // Panel inferior con controles y errores
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);

        // Panel de opciones (idiomas y botones)
        JPanel panelOpciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelOpciones.setBackground(Color.WHITE);
        panelOpciones.add(new JLabel("Idioma Origen:"));
        comboOrigen = new JComboBox<>(new String[]{"Java", "C++", "JS"});
        panelOpciones.add(comboOrigen);
        panelOpciones.add(new JLabel("Idioma Destino:"));
        comboDestino = new JComboBox<>(new String[]{"Java", "C++", "JS"});
        panelOpciones.add(comboDestino);

        btnTraducir = new JButton("Traducir");
        btnTraducir.setBackground(new Color(33, 150, 243));
        btnTraducir.setForeground(Color.WHITE);
        btnTraducir.setFocusPainted(false);
        btnTraducir.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnTraducir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                traducirCodigo();
            }
        });
        panelOpciones.add(btnTraducir);

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBackground(new Color(76, 175, 80));
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnLimpiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarCodigoFuente();
            }
        });
        panelOpciones.add(btnLimpiar);

        btnNuevo = new JButton("Nuevo");
        btnNuevo.setBackground(new Color(255, 193, 7));
        btnNuevo.setForeground(Color.WHITE);
        btnNuevo.setFocusPainted(false);
        btnNuevo.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnNuevo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reiniciarVentanas();
            }
        });
        panelOpciones.add(btnNuevo);

        bottomPanel.add(panelOpciones, BorderLayout.NORTH);

        modeloErrores = new DefaultTableModel(new Object[]{"Linea", "Error"}, 0);
        tablaErrores = new JTable(modeloErrores);
        JScrollPane scrollErrores = new JScrollPane(tablaErrores);
        scrollErrores.setBorder(BorderFactory.createTitledBorder("Errores"));
        scrollErrores.setPreferredSize(new Dimension(0, 150));

        lblMensajeError = new JLabel("");
        lblMensajeError.setForeground(Color.RED);
        lblMensajeError.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JPanel panelErrores = new JPanel(new BorderLayout());
        panelErrores.setBackground(Color.WHITE);
        panelErrores.add(scrollErrores, BorderLayout.CENTER);
        panelErrores.add(lblMensajeError, BorderLayout.SOUTH);

        bottomPanel.add(panelErrores, BorderLayout.CENTER);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void configurarLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar Nimbus: " + e.getMessage());
        }
    }

    private void configurarFuentes() {
        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("TextArea.font", new Font("Consolas", Font.PLAIN, 14));
        UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Table.headerFont", new Font("Segoe UI", Font.BOLD, 14));
    }

    private JLabel crearHeader() {
        JLabel lblHeader = new JLabel("Traductor de Código", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblHeader.setForeground(new Color(33, 150, 243));
        return lblHeader;
    }

    private JTextArea crearTextArea() {
        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Consolas", Font.PLAIN, 16));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        return textArea;
    }

    private void traducirCodigo() {
        modeloErrores.setRowCount(0);
        lblMensajeError.setText("");

        String codigo = txtCodigo.getText();
        if(codigo.equals(PLACEHOLDER)) {
            codigo = "";
        }
        
        String idiomaOrigen = (String) comboOrigen.getSelectedItem();
        String idiomaDestino = (String) comboDestino.getSelectedItem();

        if (codigo.trim().isEmpty()) {
            lblMensajeError.setText("Por favor ingresa el código fuente.");
            return;
        }

        // Se realiza la traducción. Si existen errores (por ejemplo, lenguaje incorrecto),
        // se devolverá un resultado con errores y no se mostrará la traducción.
        try {
            Traductor.ResultadoTraduccion resultado = Traductor.traducir(codigo, idiomaOrigen, idiomaDestino);
            List<Traductor.ErrorInfo> errores = resultado.errores;
            if (!errores.isEmpty()) {
                for (Traductor.ErrorInfo error : errores) {
                    modeloErrores.addRow(new Object[]{error.linea, error.mensaje});
                }
                lblMensajeError.setText("El código fuente a traducir es incorrecto o no es el lenguaje que se espera.");
                return;
            }
            // Si no hay errores, se muestra el código traducido en otra ventana
            mostrarTraduccion(resultado.traduccion);
        } catch (Exception e) {
            lblMensajeError.setText("Error durante la traducción: " + e.getMessage());
        }
    }

    // Muestra la ventana del código traducido y botón "Copiar"
    private void mostrarTraduccion(String codigoTraducido) {
        if (ventanaTraduccion != null) {
            ventanaTraduccion.dispose();
        }
        ventanaTraduccion = new JFrame("Código Traducido");
        ventanaTraduccion.setSize(600, 400);
        ventanaTraduccion.setLocationRelativeTo(null);
        ventanaTraduccion.setLayout(new BorderLayout());
        
        JTextArea txtTraducido = new JTextArea();
        txtTraducido.setEditable(false);
        txtTraducido.setFont(new Font("Consolas", Font.PLAIN, 16));
        txtTraducido.setText(codigoTraducido);
        txtTraducido.setLineWrap(true);
        txtTraducido.setWrapStyleWord(true);
        JScrollPane scrollTraducido = new JScrollPane(txtTraducido);
        scrollTraducido.setBorder(BorderFactory.createTitledBorder("Código Traducido"));
        ventanaTraduccion.add(scrollTraducido, BorderLayout.CENTER);
        
        // Panel inferior para el botón "Copiar"
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCopiar = new JButton("Copiar");
        btnCopiar.setBackground(new Color(33, 150, 243));
        btnCopiar.setForeground(Color.WHITE);
        btnCopiar.setFocusPainted(false);
        btnCopiar.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btnCopiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StringSelection stringSelection = new StringSelection(txtTraducido.getText());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
                JOptionPane.showMessageDialog(ventanaTraduccion, "Código copiado al portapapeles.");
            }
        });
        panelBotones.add(btnCopiar);
        ventanaTraduccion.add(panelBotones, BorderLayout.SOUTH);
        
        ventanaTraduccion.setVisible(true);
    }

    // Limpia únicamente el área de código fuente
    private void limpiarCodigoFuente() {
        txtCodigo.setText(PLACEHOLDER);
        txtCodigo.setForeground(Color.GRAY);
    }

    // Reinicia ambas ventanas: limpia el código fuente, los errores y cierra la ventana de traducción si existe
    private void reiniciarVentanas() {
        txtCodigo.setText(PLACEHOLDER);
        txtCodigo.setForeground(Color.GRAY);
        modeloErrores.setRowCount(0);
        lblMensajeError.setText("");
        if (ventanaTraduccion != null) {
            ventanaTraduccion.dispose();
            ventanaTraduccion = null;
        }
    }
}
