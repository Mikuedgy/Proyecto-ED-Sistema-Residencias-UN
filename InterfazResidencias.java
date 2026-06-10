import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

/**
 * InterfazResidencias
 *
 * Mejoras respecto a la versión anterior:
 *  - Stats en memoria (O(1)) en lugar de clonar el heap cada vez.
 *  - Asignación (caso 3) no destruye el heap: usa un índice de posición.
 *  - Panel "Estructuras" que muestra en tiempo real la altura del AVL
 *    y la distribución de PBM del heap (barras proporcionales).
 *  - Log de asignaciones detallado (reemplaza el JOptionPane genérico).
 *  - Feedback de errores inline bajo cada campo (no interrumpe con dialog).
 *  - Tabla con row-hover animado via Timer.
 *  - Diseño bonito :D.
 */
public class InterfazResidencias extends JFrame {

    // ── Estructuras ───────────────────────────────────────────────────────────
    private final AVLTree<Estudiante>  avl  = new AVLTree<>();
    private final MinHeap<Estudiante>  heap = new MinHeap<>();

    // ── Contadores en memoria (O(1)) ──────────────────────────────────────────
    private int statTotal     = 0;
    private int statAsignados = 0;
    private int cupos         = 0;

    // ── Stats labels ──────────────────────────────────────────────────────────
    private JLabel lblTotal, lblAsignados, lblPendientes, lblCupos;

    // ── Campos sidebar ────────────────────────────────────────────────────────
    private JTextField txtNombre, txtId, txtPbm;
    private JTextField txtCupos;
    private JTextField txtBuscarId;
    private JTextField txtActualizarId, txtNuevoPbm;
    private JTextField txtEliminarId;
    private JLabel     errNombre, errId, errPbm, errCupos, errBuscar,
            errActId, errActPbm, errElim;

    // ── Tabla ─────────────────────────────────────────────────────────────────
    private JTable            tabla;
    private DefaultTableModel modelo;

    // ── Panel lateral de estructuras ──────────────────────────────────────────
    private JPanel    panelEstructuras;
    private JPanel    centroPanelRef;          // referencia directa — evita cast frágil
    private boolean   estructurasVisible = false;
    private JLabel    lblAlturaAVL, lblSizeHeap;
    private JPanel    barrasHeap;
    private JTextArea logAsignaciones;

    // ── Hover de tabla ────────────────────────────────────────────────────────
    private int hoverRow = -1;

    // ══════════════════════════════════════════════════════════════════════════
    //  PALETA
    // ══════════════════════════════════════════════════════════════════════════
    private static final Color BG0        = new Color(0x0D, 0x11, 0x17);   // fondo principal
    private static final Color BG1        = new Color(0x16, 0x1B, 0x22);   // sidebar
    private static final Color BG2        = new Color(0x1C, 0x22, 0x2E);   // sección
    private static final Color BG3        = new Color(0x10, 0x14, 0x1A);   // input
    private static final Color BG_CARD    = new Color(0x13, 0x18, 0x20);   // cards stats
    private static final Color BG_ROW_ALT = new Color(0x0F, 0x13, 0x19);
    private static final Color BG_HOVER   = new Color(0x1A, 0x25, 0x35);

    private static final Color TEAL       = new Color(0x2D, 0xD4, 0xBF);   // acento primario
    private static final Color TEAL_DIM   = new Color(0x0D, 0x3D, 0x38);
    private static final Color TEAL_MID   = new Color(0x0D, 0x94, 0x88);
    private static final Color VIOLET     = new Color(0xA7, 0x8B, 0xFA);   // acento AVL
    private static final Color VIOLET_DIM = new Color(0x2E, 0x1B, 0x5E);
    private static final Color GREEN      = new Color(0x4A, 0xDE, 0x80);
    private static final Color GREEN_DIM  = new Color(0x14, 0x53, 0x2D);
    private static final Color AMBER      = new Color(0xFB, 0xBF, 0x24);
    private static final Color AMBER_DIM  = new Color(0x45, 0x1A, 0x03);
    private static final Color RED        = new Color(0xF8, 0x71, 0x71);
    private static final Color RED_DIM    = new Color(0x45, 0x0A, 0x0A);
    private static final Color BLUE       = new Color(0x60, 0xA5, 0xFA);

    private static final Color TEXT0      = new Color(0xE6, 0xED, 0xF3);
    private static final Color TEXT1      = new Color(0x8B, 0x94, 0x9E);
    private static final Color TEXT2      = new Color(0x58, 0x6A, 0x7E);
    private static final Color BORDER     = new Color(0x30, 0x36, 0x3D);
    private static final Color BORDER2    = new Color(0x37, 0x41, 0x5A);

    // ══════════════════════════════════════════════════════════════════════════
    public InterfazResidencias() {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}

        setTitle("Residencias · UNAL — AVL + MinHeap");
        setSize(1380, 820);
        setMinimumSize(new Dimension(1100, 660));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG0);

        construirUI();
        cargarDatosPrueba();
        setVisible(true);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  CONSTRUCCIÓN
    // ══════════════════════════════════════════════════════════════════════════
    private void construirUI() {
        JPanel raiz = new JPanel(new BorderLayout(0, 0));
        raiz.setBackground(BG0);
        raiz.setBorder(BorderFactory.createEmptyBorder(12, 14, 10, 14));

        JPanel norte = new JPanel(new BorderLayout(0, 10));
        norte.setBackground(BG0);
        norte.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        norte.add(crearTopBar(), BorderLayout.NORTH);
        norte.add(crearStats(),  BorderLayout.CENTER);

        centroPanelRef = new JPanel(new BorderLayout(12, 0));
        centroPanelRef.setBackground(BG0);
        centroPanelRef.add(crearSidebar(),      BorderLayout.WEST);
        centroPanelRef.add(crearPanelCentral(), BorderLayout.CENTER);

        panelEstructuras = crearPanelEstructuras();

        raiz.add(norte,          BorderLayout.NORTH);
        raiz.add(centroPanelRef, BorderLayout.CENTER);
        add(raiz);
    }

    // ── Top bar ───────────────────────────────────────────────────────────────
    private JPanel crearTopBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG0);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(0, 0, 10, 0)
        ));

        // Izquierda: título + subtítulo
        JLabel titulo = new JLabel("Residencias Estudiantiles  ·  UNAL");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(TEXT0);

        JLabel sub = new JLabel("Asignación prioritaria por PBM  ·  Estructuras: AVL-Tree + Min-Heap");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sub.setForeground(TEXT2);

        JPanel izq = new JPanel();
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));
        izq.setBackground(BG0);
        izq.add(titulo);
        izq.add(Box.createVerticalStrut(3));
        izq.add(sub);

        // Derecha: badge + botón panel estructuras
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setBackground(BG0);

        JButton btnEstructuras = new JButton("⬡  Ver estructuras");
        btnEstructuras.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnEstructuras.setForeground(VIOLET);
        btnEstructuras.setBackground(VIOLET_DIM);
        btnEstructuras.setOpaque(true);
        btnEstructuras.setFocusPainted(false);
        btnEstructuras.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(VIOLET.darker(), 1, true),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        btnEstructuras.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEstructuras.addActionListener(e -> toggleEstructuras());

        JLabel badge = new JLabel("● Sistema activo");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(TEAL);
        badge.setBackground(TEAL_DIM);
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TEAL_MID, 1, true),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));

        der.add(btnEstructuras);
        der.add(badge);

        p.add(izq, BorderLayout.WEST);
        p.add(der, BorderLayout.EAST);
        return p;
    }

    // ── Stats cards ───────────────────────────────────────────────────────────
    private JPanel crearStats() {
        JPanel p = new JPanel(new GridLayout(1, 4, 10, 0));
        p.setBackground(BG0);

        lblTotal     = new JLabel("0");
        lblAsignados = new JLabel("0");
        lblPendientes= new JLabel("0");
        lblCupos     = new JLabel("0");

        p.add(statCard("Total registrados",  lblTotal,      BLUE,  "Árbol AVL"));
        p.add(statCard("Con residencia",      lblAsignados,  GREEN, "Min-Heap asignados"));
        p.add(statCard("Sin asignar",         lblPendientes, AMBER, "En cola de espera"));
        p.add(statCard("Cupos disponibles",   lblCupos,      TEAL,  "Definidos manualmente"));
        return p;
    }

    private JPanel statCard(String titulo, JLabel val, Color color, String nota) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, color),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER, 1),
                        BorderFactory.createEmptyBorder(10, 14, 10, 14)
                )
        ));
        JLabel lTitulo = new JLabel(titulo);
        lTitulo.setForeground(TEXT1);
        lTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        val.setFont(new Font("Segoe UI", Font.BOLD, 28));
        val.setForeground(color);

        JLabel lNota = new JLabel(nota);
        lNota.setForeground(TEXT2);
        lNota.setFont(new Font("Segoe UI", Font.PLAIN, 10));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(BG_CARD);
        top.add(lTitulo, BorderLayout.WEST);
        top.add(lNota,   BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
        return card;
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────
    private JScrollPane crearSidebar() {
        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setBackground(BG1);
        inner.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1 ── Registrar
        JPanel sReg = seccion("＋  Registrar estudiante", TEAL);
        txtNombre = campo(sReg, "Nombre completo",     "Ej: Valeria Torres");
        errNombre = errLabel(); sReg.add(errNombre);
        txtId     = campo(sReg, "ID del estudiante",   "Ej: 1050000001");
        errId     = errLabel(); sReg.add(errId);
        txtPbm    = campo(sReg, "Puntaje PBM (0–100)", "Ej: 12.5");
        errPbm    = errLabel(); sReg.add(errPbm);
        sReg.add(Box.createVerticalStrut(4));
        sReg.add(btnPrimario("Registrar estudiante", TEAL, TEAL_DIM, e -> registrar()));

        // 2 ── Cupos
        JPanel sCup = seccion("▣  Control de cupos", GREEN);
        txtCupos = campo(sCup, "Cantidad de cupos", "Ej: 10");
        errCupos = errLabel(); sCup.add(errCupos);
        sCup.add(Box.createVerticalStrut(4));
        sCup.add(btnSecundario("Guardar cupos", e -> definirCupos()));
        sCup.add(Box.createVerticalStrut(4));
        sCup.add(btnPrimario("✔  Asignar residencias", GREEN, GREEN_DIM, e -> asignarResidencias()));

        // 3 ── Buscar
        JPanel sBus = seccion("⌕  Consultar estudiante", BLUE);
        txtBuscarId = campo(sBus, "ID del estudiante", "ID");
        errBuscar   = errLabel(); sBus.add(errBuscar);
        sBus.add(Box.createVerticalStrut(4));
        sBus.add(btnSecundario("Buscar", e -> buscarEstudiante()));

        // 4 ── Actualizar PBM
        JPanel sAct = seccion("✎  Actualizar PBM", AMBER);
        txtActualizarId = campo(sAct, "ID del estudiante",  "ID");
        errActId        = errLabel(); sAct.add(errActId);
        txtNuevoPbm     = campo(sAct, "Nuevo PBM (0–100)", "Ej: 8.0");
        errActPbm       = errLabel(); sAct.add(errActPbm);
        sAct.add(Box.createVerticalStrut(4));
        sAct.add(btnSecundario("Actualizar PBM", e -> actualizarPbm()));

        // 5 ── Eliminar
        JPanel sElim = seccion("✕  Eliminar estudiante", RED);
        txtEliminarId = campo(sElim, "ID del estudiante", "ID");
        errElim       = errLabel(); sElim.add(errElim);
        sElim.add(Box.createVerticalStrut(4));
        sElim.add(btnPrimario("Eliminar estudiante", RED, RED_DIM, e -> eliminarEstudiante()));

        inner.add(sReg);  inner.add(gap());
        inner.add(sCup);  inner.add(gap());
        inner.add(sBus);  inner.add(gap());
        inner.add(sAct);  inner.add(gap());
        inner.add(sElim);
        inner.add(Box.createVerticalGlue());

        JScrollPane sp = new JScrollPane(inner,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setBorder(BorderFactory.createLineBorder(BORDER2, 1, true));
        sp.getViewport().setBackground(BG1);
        sp.setPreferredSize(new Dimension(296, 0));
        sp.getVerticalScrollBar().setUnitIncrement(18);
        return sp;
    }

    // ── Panel central: tabla + log ────────────────────────────────────────────
    private JPanel crearPanelCentral() {
        JPanel wrap = new JPanel(new BorderLayout(0, 8));
        wrap.setBackground(BG0);

        // Cabecera filtros
        JPanel cab = new JPanel(new BorderLayout(10, 0));
        cab.setBackground(BG_CARD);
        cab.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        JLabel tituloTabla = new JLabel("≡  Lista de estudiantes");
        tituloTabla.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tituloTabla.setForeground(TEAL);

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        filtros.setBackground(BG_CARD);

        JLabel lBuscar = new JLabel("Filtrar:");
        lBuscar.setForeground(TEXT1);
        lBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JTextField txtFiltro = new JTextField(14);
        estilizarInput(txtFiltro, BG3);

        JComboBox<String> cbEstado = new JComboBox<>(new String[]{"Todos","Con residencia","Sin asignar"});
        JComboBox<String> cbOrden  = new JComboBox<>(new String[]{"PBM ↑","PBM ↓","ID","Nombre"});
        estilizarCombo(cbEstado, BG3);
        estilizarCombo(cbOrden,  BG3);

        filtros.add(lBuscar); filtros.add(txtFiltro);
        filtros.add(cbEstado); filtros.add(cbOrden);

        cab.add(tituloTabla, BorderLayout.WEST);
        cab.add(filtros,     BorderLayout.EAST);

        // Tabla
        String[] cols = {"#", "ID", "Nombre", "PBM", "Estado"};
        modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        estilizarTabla();

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        scrollTabla.getViewport().setBackground(BG0);
        scrollTabla.getVerticalScrollBar().setUnitIncrement(16);

        // Log de asignaciones
        logAsignaciones = new JTextArea(4, 0);
        logAsignaciones.setEditable(false);
        logAsignaciones.setBackground(new Color(0x0A, 0x10, 0x16));
        logAsignaciones.setForeground(TEXT1);
        logAsignaciones.setFont(new Font("JetBrains Mono", Font.PLAIN, 11));
        logAsignaciones.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        logAsignaciones.setText("— Log de asignaciones —\n");

        JScrollPane scrollLog = new JScrollPane(logAsignaciones);
        scrollLog.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));
        scrollLog.setPreferredSize(new Dimension(0, 100));

        JPanel centro = new JPanel(new BorderLayout(0, 0));
        centro.setBackground(BG0);
        centro.add(scrollTabla, BorderLayout.CENTER);
        centro.add(scrollLog,   BorderLayout.SOUTH);

        // Listeners filtros
        Runnable refrescar = () -> refrescarTablaFiltrada(
                txtFiltro.getText().toLowerCase(),
                cbEstado.getSelectedIndex(),
                cbOrden.getSelectedIndex()
        );
        txtFiltro.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { refrescar.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { refrescar.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { refrescar.run(); }
        });
        cbEstado.addActionListener(e -> refrescar.run());
        cbOrden.addActionListener(e  -> refrescar.run());

        wrap.add(cab,    BorderLayout.NORTH);
        wrap.add(centro, BorderLayout.CENTER);
        return wrap;
    }

    // ── Panel lateral de estructuras ──────────────────────────────────────────
    private JPanel crearPanelEstructuras() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG1);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, BORDER2),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));
        p.setPreferredSize(new Dimension(240, 0));

        JLabel titulo = new JLabel("⬡  Estado interno");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titulo.setForeground(VIOLET);
        titulo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subAVL = seccionLbl("AVL Tree", VIOLET);
        lblAlturaAVL = infoLbl("Altura: —");

        JLabel subHeap = seccionLbl("Min-Heap (PBM)", TEAL);
        lblSizeHeap = infoLbl("Tamaño: —");

        JLabel subDist = seccionLbl("Distribución PBM", TEXT1);
        barrasHeap = new JPanel();
        barrasHeap.setLayout(new BoxLayout(barrasHeap, BoxLayout.Y_AXIS));
        barrasHeap.setBackground(BG1);
        barrasHeap.setAlignmentX(LEFT_ALIGNMENT);

        p.add(titulo);
        p.add(Box.createVerticalStrut(14));
        p.add(subAVL); p.add(Box.createVerticalStrut(4));
        p.add(lblAlturaAVL); p.add(Box.createVerticalStrut(12));
        p.add(subHeap); p.add(Box.createVerticalStrut(4));
        p.add(lblSizeHeap); p.add(Box.createVerticalStrut(12));
        p.add(subDist); p.add(Box.createVerticalStrut(6));
        p.add(barrasHeap);
        p.add(Box.createVerticalGlue());

        return p;
    }

    private void toggleEstructuras() {
        if (estructurasVisible) {
            centroPanelRef.remove(panelEstructuras);
            estructurasVisible = false;
        } else {
            estructurasVisible = true;          // flip ANTES de actualizar para que el guard no bloquee
            actualizarPanelEstructuras();
            centroPanelRef.add(panelEstructuras, BorderLayout.EAST);
        }
        centroPanelRef.revalidate();
        centroPanelRef.repaint();
    }

    private void actualizarPanelEstructuras() {
        if (!estructurasVisible) return;

        // Altura AVL: getHeight() lee el campo height del nodo raíz — O(1).
        int n      = statTotal;
        int altura = avl.getHeight();
        lblAlturaAVL.setText("Altura: " + altura + "  (n=" + n + ")");

        // Tamaño heap
        lblSizeHeap.setText("Tamaño: " + heap.getSize() + "  (O(log n) ops)");

        // Distribución PBM en 5 rangos
        barrasHeap.removeAll();
        int[] rangos = new int[5]; // [0-4, 4-8, 8-12, 12-16, 16-20]
        MinHeap<Estudiante> copia = heap.clonar();
        while (!copia.isEmpty()) {
            double pbm = copia.ExtractMin().getPbm();
            int r = Math.min((int)(pbm / 4), 4);
            rangos[r]++;
        }
        int maxR = 1;
        for (int v : rangos) if (v > maxR) maxR = v;
        String[] etqs = {"0–4","4–8","8–12","12–16","16–20"};
        for (int i = 0; i < 5; i++) {
            int val = rangos[i];
            JPanel fila = new JPanel(new BorderLayout(4, 0));
            fila.setBackground(BG1);
            fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
            fila.setAlignmentX(LEFT_ALIGNMENT);

            JLabel etq = new JLabel(etqs[i]);
            etq.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            etq.setForeground(TEXT2);
            etq.setPreferredSize(new Dimension(38, 14));

            int barW = (int)((val / (double) maxR) * 140);
            JPanel barra = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(TEAL_DIM);
                    g2.fillRoundRect(0, 2, getWidth(), getHeight() - 4, 4, 4);
                    g2.setColor(TEAL);
                    g2.fillRoundRect(0, 2, barW, getHeight() - 4, 4, 4);
                }
            };
            barra.setBackground(BG1);
            barra.setPreferredSize(new Dimension(140, 14));

            JLabel cnt = new JLabel(String.valueOf(val));
            cnt.setFont(new Font("Segoe UI", Font.BOLD, 10));
            cnt.setForeground(TEAL);
            cnt.setPreferredSize(new Dimension(22, 14));

            fila.add(etq,   BorderLayout.WEST);
            fila.add(barra, BorderLayout.CENTER);
            fila.add(cnt,   BorderLayout.EAST);
            barrasHeap.add(fila);
            barrasHeap.add(Box.createVerticalStrut(4));
        }
        barrasHeap.revalidate();
        barrasHeap.repaint();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  OPERACIONES
    // ══════════════════════════════════════════════════════════════════════════

    // Caso 1: Registrar
    private void registrar() {
        clearErrors(errNombre, errId, errPbm);
        boolean ok = true;

        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) { showErr(errNombre, "El nombre no puede estar vacío"); ok = false; }

        long id = 0;
        try { id = Long.parseLong(txtId.getText().trim()); }
        catch (NumberFormatException ex) { showErr(errId, "ID inválido — solo números"); ok = false; }

        double pbm = 0;
        try {
            pbm = Double.parseDouble(txtPbm.getText().trim());
            if (pbm < 0 || pbm > 100) { showErr(errPbm, "PBM debe estar entre 0 y 100"); ok = false; }
        } catch (NumberFormatException ex) { showErr(errPbm, "PBM inválido — número decimal"); ok = false; }

        if (!ok) return;

        if (avl.searchById(id) != null) { showErr(errId, "Ya existe un estudiante con ese ID"); return; }

        Estudiante e = new Estudiante(nombre, id, pbm);
        avl.insert(e);
        heap.Insert(e);
        statTotal++;
        recargarTabla();
        limpiar(txtNombre, txtId, txtPbm);
        actualizarPanelEstructuras();
        flashStat(lblTotal, BLUE);
    }

    // Caso 2: Definir cupos
    private void definirCupos() {
        clearErrors(errCupos);
        try {
            int v = Integer.parseInt(txtCupos.getText().trim());
            if (v < 0) { showErr(errCupos, "No puede ser negativo"); return; }
            cupos = v;
            actualizarStats();
            limpiar(txtCupos);
        } catch (NumberFormatException ex) { showErr(errCupos, "Número entero inválido"); }
    }

    // Caso 3: Asignar residencias
    // MEJORA: itera el heap clonado sin destruirlo. Solo modifica el objeto
    // (mutación compartida AVL+Heap), que es exactamente lo que hace el menú CLI.
    private void asignarResidencias() {
        if (cupos == 0) { flashErr("No hay cupos disponibles"); return; }

        int asignados = 0;
        StringBuilder log = new StringBuilder();
        MinHeap<Estudiante> copia = heap.clonar();

        while (cupos > 0 && !copia.isEmpty()) {
            Estudiante e = copia.ExtractMin();
            if (!e.getHasResidency()) {
                e.setTieneResidencia(true);
                cupos--;
                asignados++;
                statAsignados++;
                log.append("  ✔ ")
                        .append(e.getNombre())
                        .append("  (ID: ").append(e.getId())
                        .append(", PBM: ").append(String.format("%.1f", e.getPbm()))
                        .append(")\n");
            }
        }

        recargarTabla();
        actualizarPanelEstructuras();

        if (asignados == 0) {
            appendLog("— Todos los estudiantes ya tienen residencia asignada —");
        } else {
            appendLog("[Asignación] " + asignados + " cupo(s) asignados:\n" + log);
            flashStat(lblAsignados, GREEN);
        }
    }

    // Caso 4: Buscar
    private void buscarEstudiante() {
        clearErrors(errBuscar);
        long id;
        try { id = Long.parseLong(txtBuscarId.getText().trim()); }
        catch (NumberFormatException ex) { showErr(errBuscar, "ID inválido"); return; }

        Estudiante e = avl.searchById(id);
        if (e == null) { showErr(errBuscar, "No existe estudiante con ID " + id); return; }

        String msg = "<html><table>"
                + "<tr><td><b>Nombre</b></td><td>&nbsp;" + e.getNombre() + "</td></tr>"
                + "<tr><td><b>ID</b></td><td>&nbsp;" + e.getId() + "</td></tr>"
                + "<tr><td><b>PBM</b></td><td>&nbsp;" + e.getPbm() + "</td></tr>"
                + "<tr><td><b>Estado</b></td><td>&nbsp;" + (e.getHasResidency() ? "✔ Con residencia" : "⏳ Sin asignar") + "</td></tr>"
                + "</table></html>";
        JOptionPane.showMessageDialog(this, msg, "Estudiante encontrado — Búsqueda AVL O(log n)", JOptionPane.INFORMATION_MESSAGE);
        limpiar(txtBuscarId);
    }

    // Caso 5: Actualizar PBM
    private void actualizarPbm() {
        clearErrors(errActId, errActPbm);
        long id;
        try { id = Long.parseLong(txtActualizarId.getText().trim()); }
        catch (NumberFormatException ex) { showErr(errActId, "ID inválido"); return; }

        double pbm;
        try {
            pbm = Double.parseDouble(txtNuevoPbm.getText().trim());
            if (pbm < 0 || pbm > 100) { showErr(errActPbm, "PBM debe estar entre 0 y 100"); return; }
        } catch (NumberFormatException ex) { showErr(errActPbm, "PBM inválido"); return; }

        Estudiante e = avl.searchById(id);
        if (e == null) { showErr(errActId, "Estudiante no encontrado"); return; }

        // Sacar del heap, mutar, reinsertar — idéntico al CLI
        heap.removeByEstudiante(e);
        e.setPbm(pbm);
        heap.Insert(e);

        recargarTabla();
        limpiar(txtActualizarId, txtNuevoPbm);
        actualizarPanelEstructuras();
        appendLog("[PBM actualizado] " + e.getNombre() + " → " + pbm);
    }

    // Caso 6: Eliminar
    private void eliminarEstudiante() {
        clearErrors(errElim);
        long id;
        try { id = Long.parseLong(txtEliminarId.getText().trim()); }
        catch (NumberFormatException ex) { showErr(errElim, "ID inválido"); return; }

        Estudiante e = avl.searchById(id);
        if (e == null) { showErr(errElim, "No existe estudiante con ID " + id); return; }

        int ok = JOptionPane.showConfirmDialog(this,
                "¿Eliminar a " + e.getNombre() + " (ID: " + id + ")?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok != JOptionPane.YES_OPTION) return;

        avl.delete(e);
        heap.removeByEstudiante(e);
        statTotal--;
        if (e.getHasResidency()) statAsignados--;

        recargarTabla();
        limpiar(txtEliminarId);
        actualizarPanelEstructuras();
        appendLog("[Eliminado] " + e.getNombre() + " (ID: " + id + ")");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  TABLA Y ESTADÍSTICAS
    // ══════════════════════════════════════════════════════════════════════════
    private void recargarTabla() {
        modelo.setRowCount(0);
        MinHeap<Estudiante> copia = heap.clonar();
        int rank = 1;
        while (!copia.isEmpty()) {
            Estudiante e = copia.ExtractMin();
            modelo.addRow(new Object[]{
                    rank++, e.getId(), e.getNombre(),
                    String.format("%.1f", e.getPbm()),
                    e.getHasResidency() ? "Asignado" : "Pendiente"
            });
        }
        actualizarStats();
    }

    private void refrescarTablaFiltrada(String txt, int estadoIdx, int ordenIdx) {
        MinHeap<Estudiante> copia = heap.clonar();
        java.util.List<Estudiante> lista = new java.util.ArrayList<>();
        while (!copia.isEmpty()) lista.add(copia.ExtractMin());

        if (estadoIdx == 1) lista.removeIf(e -> !e.getHasResidency());
        if (estadoIdx == 2) lista.removeIf(Estudiante::getHasResidency);
        if (!txt.isEmpty()) lista.removeIf(e ->
                !e.getNombre().toLowerCase().contains(txt) &&
                        !String.valueOf(e.getId()).contains(txt));

        switch (ordenIdx) {
            case 0 -> lista.sort((a, b) -> Double.compare(a.getPbm(), b.getPbm()));
            case 1 -> lista.sort((a, b) -> Double.compare(b.getPbm(), a.getPbm()));
            case 2 -> lista.sort((a, b) -> Long.compare(a.getId(), b.getId()));
            case 3 -> lista.sort((a, b) -> a.getNombre().compareTo(b.getNombre()));
        }

        modelo.setRowCount(0);
        int rank = 1;
        for (Estudiante e : lista)
            modelo.addRow(new Object[]{
                    rank++, e.getId(), e.getNombre(),
                    String.format("%.1f", e.getPbm()),
                    e.getHasResidency() ? "Asignado" : "Pendiente"
            });
    }

    // Stats en O(1) — usa contadores en memoria
    private void actualizarStats() {
        lblTotal.setText(String.valueOf(statTotal));
        lblAsignados.setText(String.valueOf(statAsignados));
        lblPendientes.setText(String.valueOf(statTotal - statAsignados));
        lblCupos.setText(String.valueOf(cupos));
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  DATOS DE PRUEBA
    // ══════════════════════════════════════════════════════════════════════════
    private void cargarDatosPrueba() {
        Random rand = new Random(42);
        String[] nombres   = {"Ana","Luis","María","Carlos","Sofía","Diego","Valentina","Andrés",
                "Isabella","Santiago","Camila","Mateo","Daniela","Sebastián","Mariana",
                "Felipe","Laura","Nicolás","Paula","Alejandro"};
        String[] apellidos = {"Torres","García","López","Ruiz","Méndez","Herrera","Cruz","Mora",
                "Díaz","Reyes","Vargas","Castillo","Romero","Jiménez","Morales",
                "Suárez","Ramírez","Flores","Núñez","Vega"};
        for (int i = 0; i < 50; i++) {
            long   id  = 1000 + i;
            String nom = nombres[rand.nextInt(nombres.length)] + " " + apellidos[rand.nextInt(apellidos.length)];
            double pbm = Math.round((rand.nextDouble() * 20) * 10.0) / 10.0;
            Estudiante e = new Estudiante(nom, id, pbm);
            avl.insert(e);
            heap.Insert(e);
            statTotal++;
        }
        recargarTabla();
        actualizarPanelEstructuras();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  HELPERS DE UI
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel seccion(String titulo, Color acento) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(BG2);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 2, 0, 0, acento),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER2, 1),
                        BorderFactory.createEmptyBorder(10, 12, 12, 12)
                )
        ));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        p.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(acento);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        p.add(lbl);
        p.add(Box.createVerticalStrut(10));
        return p;
    }

    private JTextField campo(JPanel sec, String etiqueta, String placeholder) {
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(TEXT2);
        lbl.setAlignmentX(LEFT_ALIGNMENT);

        JTextField tf = new JTextField();
        tf.setToolTipText(placeholder);
        estilizarInput(tf, BG3);
        tf.setAlignmentX(LEFT_ALIGNMENT);
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        sec.add(lbl);
        sec.add(Box.createVerticalStrut(2));
        sec.add(tf);
        sec.add(Box.createVerticalStrut(2));
        return tf;
    }

    private JLabel errLabel() {
        JLabel l = new JLabel(" ");
        l.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        l.setForeground(RED);
        l.setAlignmentX(LEFT_ALIGNMENT);
        l.setBorder(BorderFactory.createEmptyBorder(0, 2, 4, 0));
        return l;
    }

    private JLabel seccionLbl(String txt, Color color) {
        JLabel l = new JLabel(txt.toUpperCase());
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(color);
        l.setAlignmentX(LEFT_ALIGNMENT);
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
        return l;
    }

    private JLabel infoLbl(String txt) {
        JLabel l = new JLabel(txt);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(TEXT0);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private Component gap() { return Box.createVerticalStrut(8); }

    private JButton btnPrimario(String texto, Color fg, Color bg,
                                java.awt.event.ActionListener al) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setForeground(fg);
        b.setBackground(bg);
        b.setOpaque(true);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(fg.darker(), 1, true),
                BorderFactory.createEmptyBorder(7, 12, 7, 12)
        ));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        b.addActionListener(al);
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(bg.darker()); }
            public void mouseExited(java.awt.event.MouseEvent e)  { b.setBackground(bg); }
        });
        return b;
    }

    private JButton btnSecundario(String texto, java.awt.event.ActionListener al) {
        Color bg  = new Color(0x2D, 0x34, 0x48);
        Color bdr = BORDER2;
        JButton b = new JButton(texto);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setForeground(TEXT0);
        b.setBackground(bg);
        b.setOpaque(true);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bdr, 1, true),
                BorderFactory.createEmptyBorder(7, 12, 7, 12)
        ));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        b.addActionListener(al);
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { b.setBackground(bg.brighter()); }
            public void mouseExited(java.awt.event.MouseEvent e)  { b.setBackground(bg); }
        });
        return b;
    }

    private void estilizarInput(JTextField tf, Color bg) {
        tf.setBackground(bg);
        tf.setForeground(TEXT0);
        tf.setCaretColor(TEAL);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER2, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
    }

    private void estilizarCombo(JComboBox<String> cb, Color bg) {
        cb.setBackground(bg);
        cb.setForeground(TEXT0);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cb.setFocusable(false);
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object val,
                                                          int idx, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, val, idx, sel, focus);
                setBackground(sel ? BG_CARD : bg);
                setForeground(TEXT0);
                setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                return this;
            }
        });
    }

    private void estilizarTabla() {
        tabla.setBackground(BG0);
        tabla.setForeground(TEXT0);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(36);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setSelectionBackground(new Color(0x21, 0x32, 0x46));
        tabla.setSelectionForeground(TEXT0);
        tabla.setFillsViewportHeight(true);

        // Hover de filas
        tabla.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                int r = tabla.rowAtPoint(e.getPoint());
                if (r != hoverRow) { hoverRow = r; tabla.repaint(); }
            }
        });
        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                hoverRow = -1; tabla.repaint();
            }
        });

        JTableHeader hdr = tabla.getTableHeader();
        hdr.setBackground(BG_CARD);
        hdr.setForeground(TEXT1);
        hdr.setFont(new Font("Segoe UI", Font.BOLD, 11));
        hdr.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        hdr.setReorderingAllowed(false);

        tabla.getColumnModel().getColumn(0).setPreferredWidth(36);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(88);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(220);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(66);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(110);

        // Renderer Estado
        tabla.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                                                           boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                setHorizontalAlignment(CENTER);
                setFont(new Font("Segoe UI", Font.BOLD, 11));
                Color bg = (row == hoverRow && !sel) ? BG_HOVER
                        : sel ? new Color(0x21, 0x32, 0x46)
                          : (row % 2 == 0 ? BG0 : BG_ROW_ALT);
                if ("Asignado".equals(val)) {
                    setForeground(GREEN); setBackground(sel ? bg : GREEN_DIM);
                } else {
                    setForeground(AMBER); setBackground(sel ? bg : AMBER_DIM);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        });

        // Renderer general
        DefaultTableCellRenderer gen = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                                                           boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                setForeground(col == 0 ? TEXT2 : TEXT0);
                Color bg = (row == hoverRow && !sel) ? BG_HOVER
                        : sel ? new Color(0x21, 0x32, 0x46)
                          : (row % 2 == 0 ? BG0 : BG_ROW_ALT);
                setBackground(bg);
                setFont(col == 1
                        ? new Font("JetBrains Mono", Font.PLAIN, 12)
                        : new Font("Segoe UI", col == 2 ? Font.BOLD : Font.PLAIN, 13));
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                setHorizontalAlignment(col == 0 ? CENTER : LEFT);
                return this;
            }
        };
        for (int i = 0; i < 4; i++) tabla.getColumnModel().getColumn(i).setCellRenderer(gen);
    }

    // ── Feedback helpers ──────────────────────────────────────────────────────
    private void showErr(JLabel lbl, String msg) {
        lbl.setText("⚠ " + msg);
        // Auto-limpia a los 4 s
        new javax.swing.Timer(4000, e -> {
            lbl.setText(" ");
            ((javax.swing.Timer)e.getSource()).stop();
        }).start();
    }
    private void clearErrors(JLabel... lbls) { for (JLabel l : lbls) l.setText(" "); }

    private void flashErr(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Atención", JOptionPane.WARNING_MESSAGE);
    }

    private void appendLog(String txt) {
        logAsignaciones.append(txt + "\n");
        logAsignaciones.setCaretPosition(logAsignaciones.getDocument().getLength());
    }

    /** Parpadeo de color en una stat label para dar feedback visual */
    private void flashStat(JLabel lbl, Color color) {
        Color original = lbl.getForeground();
        lbl.setForeground(Color.WHITE);
        new javax.swing.Timer(200, e -> {
            lbl.setForeground(original);
            ((javax.swing.Timer)e.getSource()).stop();
        }).start();
    }

    private void limpiar(JTextField... ff) { for (JTextField f : ff) f.setText(""); }

    // ══════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) {}
        SwingUtilities.invokeLater(InterfazResidencias::new);
    }
}
