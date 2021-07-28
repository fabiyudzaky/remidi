import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.time.*;
import java.time.format.*;

public class FormPersewaan {
    private JPanel panelPersewaan;
    private JTextField textJudul;
    private JTable tablePersewaan;
    private JButton buttonSimpan;
    private JButton buttonKembali;
    private JButton buttonDelete;
    private JButton buttonEdit;
    private JLabel localDate;
    private JLabel localTime;
    private JLabel nominal;

    LocalDate today = LocalDate.now();
    LocalTime time = LocalTime.now();

    protected static final String JDBC_DRIVER;
    protected static final String DB_URL;
    protected static final String USER;
    protected static final String PASS;

    static {
        JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
        DB_URL = "jdbc:mysql://localhost/sewabuku";
        USER = "root";
        PASS = "";
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("FormPersewaan");
        frame.setContentPane(new FormPersewaan().panelPersewaan);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    protected static Connection connectDB;
    protected static Statement statmt;
    protected static ResultSet hasil;

    protected static final int biayaSewa=5000;

    public void TampilkanTabel(){
        DefaultTableCellRenderer tengah = new DefaultTableCellRenderer();
        tengah.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer judul = new DefaultTableCellRenderer();
        judul.setHorizontalAlignment(JLabel.LEADING);

        try{

            Class.forName(JDBC_DRIVER);

            connectDB = DriverManager.getConnection(DB_URL,USER,PASS);

            DefaultTableModel isiTabel = new DefaultTableModel();
            isiTabel.addColumn("ID");
            isiTabel.addColumn("Judul Buku");
            isiTabel.addColumn("Tanggal Pinjam");
            isiTabel.addColumn("Tanggal Harus Kembali");
            isiTabel.addColumn("Tanggal Kembali");
            isiTabel.addColumn("Denda");
            isiTabel.addColumn("Biaya Sewa");

            statmt = connectDB.createStatement();
            String sql = "SELECT * FROM sewabuku";

            hasil= statmt.executeQuery(sql);

            while (hasil.next()){
                isiTabel.addRow(new Object[] {
                        hasil.getString("id"),
                        hasil.getString("judul"),
                        hasil.getString("tanggal_pinjam"),
                        hasil.getString("tanggal_harus_kembali"),
                        hasil.getString("tanggal_kembali"),
                        hasil.getString("denda"),
                        hasil.getString("biaya_sewa")
                });
            }
            hasil.close();
            connectDB.close();
            statmt.close();

            tablePersewaan.setModel(isiTabel);

            tablePersewaan.getColumnModel().getColumn(0).setCellRenderer(tengah);
            tablePersewaan.getColumnModel().getColumn(1).setCellRenderer(judul);
            tablePersewaan.getColumnModel().getColumn(2).setCellRenderer(tengah);
            tablePersewaan.getColumnModel().getColumn(3).setCellRenderer(tengah);
            tablePersewaan.getColumnModel().getColumn(4).setCellRenderer(tengah);
        }catch (SQLException eksepsi){
            System.out.println(eksepsi.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void Tambah(String judul, LocalDate tanggalPinjam, LocalDate tanggalHarusKembali){
        try{

            Class.forName(JDBC_DRIVER);

            connectDB = DriverManager.getConnection(DB_URL,USER,PASS);

            String sql="INSERT INTO sewabuku (judul, tanggal_pinjam, tanggal_harus_kembali) VALUES (?, ?, ?)";

            PreparedStatement prestm = connectDB.prepareStatement(sql);

            prestm.setString(1, judul);
            prestm.setString(2, String.valueOf(tanggalPinjam));
            prestm.setString(3, String.valueOf(tanggalHarusKembali));

            prestm.execute();

            connectDB.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public FormPersewaan(){
        String tanggal = String.valueOf(today);
        localDate.setText(tanggal);

        DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("HH:mm");
        String waktu = formatTime.format(time);
        String jam = String.valueOf(waktu);
        localTime.setText(jam);

        nominal.setText("5.000");

        TampilkanTabel();

        buttonSimpan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                String isiJudul = textJudul.getText();
                textJudul.setText("");

                LocalDate isiTanggal = today;

                LocalDate isiTanggalKembali = today.plusDays(7);

                Tambah(isiJudul, isiTanggal, isiTanggalKembali);

                TampilkanTabel();
            }
        });
    }

}