import java.awt.*;

public class Tema {

    // THEME 1 - Sporty Green
    final public String warna1_utama            = "#0B6623";   // Dark Green
    final public String warna1_utama_gelap      = "#1E1E1E";   // Dark Gray
    final public String warna1_utama_lembut     = "#E0E0E0";   // Dark Gray
    final public String warna1_isi              = "#32CD32";   // Neon Green (Accent)
    final public String warna1_pelengkap        = "#E0E0E0";   // Light Gray
    
    // THEME 2 - Minimalist Navy Lime
    final public String warna2_utama            = "#0A2A43";   // Navy Blue
    final public String warna2_utama_lembut     = "#334155";   // Slate Gray
    final public String warna2_isi              = "#A3E635";   // Lime Green
    final public String warna2_pelengkap        = "#F3F4F6";   // Soft Gray
    
    // THEME 3 - Arena Black Yellow
    final public String warna3_utama            = "#000000";   // Black
    final public String warna3_utama_lembut     = "#1E1E1E";   // Dark Gray
    final public String warna3_isi              = "#FFCC00";   // Yellow Gold
    final public String warna3_pelengkap        = "#FFFFFF";   // White
    
    // yang di pake
    final public String warna_utama            = warna1_utama;   // Black
    final public String warna_utama_lembut     = warna1_utama_lembut;   // Dark Gray
    final public String warna_isi              = warna1_isi;   // Yellow Gold
    final public String warna_pelengkap        = warna1_pelengkap;   // White
    
    
    // font
    final public String font                   = "Poppins";
    final public String font2                   = "Montserrat";
    final public String font3                    = "Oswald";
    
    // Resolusi dan ukuran tampilan
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    public int lebar = 1280;
    public int tinggi = 720;

    public Tema(){}
}
