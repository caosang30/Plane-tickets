package projects.services;

import projects.models.TransactionDetail;
import projects.models.Ticket; 
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import java.io.File; 

public class InvoiceService {

    private static final String FONT_RELATIVE_PATH = "lib/arial.ttf"; 
    private static BaseFont baseFont;

    // Khởi tạo BaseFont tĩnh (ĐÃ SỬA LỖI TÌM FONT)
    static {
        try {
            // SỬ DỤNG ĐƯỜNG DẪN TUYỆT ĐỐI HÓA (Giải pháp fix lỗi font)
            String absolutePath = System.getProperty("user.dir") + File.separator + FONT_RELATIVE_PATH;
            
            baseFont = BaseFont.createFont(absolutePath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            
            System.out.println("DEBUG: Đã tải font thành công từ: " + absolutePath);
        } catch (DocumentException | IOException e) {
            System.err.println("LỖI KHÔNG TẢI ĐƯỢC FONT CHO PDF. Đảm bảo 'lib/arial.ttf' tồn tại.");
            e.printStackTrace(); 
            baseFont = null;
        }
    }

    private final Font FONT_TITLE;
    private final Font FONT_HEADER;
    private final Font FONT_NORMAL;
    private final Font FONT_MONEY;

    public InvoiceService() {
        if (baseFont != null) {
            FONT_TITLE = new Font(baseFont, 16, Font.BOLD, BaseColor.BLUE);
            FONT_HEADER = new Font(baseFont, 12, Font.BOLD);
            FONT_NORMAL = new Font(baseFont, 10, Font.NORMAL);
            FONT_MONEY = new Font(baseFont, 10, Font.BOLD, BaseColor.RED);
        } else {
            // Dùng font mặc định nếu tải font iText thất bại
            FONT_TITLE = FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD, BaseColor.BLUE);
            FONT_HEADER = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
            FONT_NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);
            FONT_MONEY = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD, BaseColor.RED);
        }
    }

    // =================================================================================
    // PHƯƠNG THỨC 1: TẠO HÓA ĐƠN ĐẶT VÉ MỚI
    // =================================================================================
    public String createInvoiceFile(Ticket ticket) {
        String filePath = String.format("./invoices/HoaDon_VeBan_%d.pdf", ticket.getMaVeBan());
        if (!checkFont()) return null;

        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        Locale vn = Locale.of("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(vn);

        try {
            new File("./invoices/").mkdirs();
            
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            Paragraph title = new Paragraph("HÓA ĐƠN GIAO DỊCH BÁN VÉ", FONT_TITLE);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("THÔNG TIN GIAO DỊCH", FONT_HEADER));
            document.add(new Paragraph("----------------------------------------------------------------------------------------------------------------------------------"));

            document.add(new Paragraph(String.format("Mã Vé Bán: %d", ticket.getMaVeBan()), FONT_NORMAL));
            document.add(new Paragraph(String.format("Chuyến Bay: %d", ticket.getMaChuyenBay()), FONT_NORMAL));
            document.add(new Paragraph(String.format("Loại Vé: %s (x%d)", ticket.getLoaiVe(), ticket.getSoLuongBan()), FONT_NORMAL));
            document.add(new Paragraph(String.format("Mã Khách Hàng: %d", ticket.getMaKhachHang()), FONT_NORMAL));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("CHI TIẾT THANH TOÁN", FONT_HEADER));
            document.add(new Paragraph("----------------------------------------------------------------------------------------------------------------------------------"));
            
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.addCell(createCell("Tổng Tiền Thanh Toán", FONT_HEADER, Element.ALIGN_LEFT));
            
            String formattedAmount = currencyFormatter.format(ticket.getThanhTien());
            table.addCell(createCell(formattedAmount, FONT_MONEY, Element.ALIGN_RIGHT));

            document.add(table);
            document.add(new Paragraph("\nCảm ơn quý khách đã sử dụng dịch vụ.", FONT_NORMAL));

            document.close();
            return filePath;

        } catch (DocumentException | IOException e) {
            System.err.println("Lỗi khi tạo file PDF cho vé bán #" + ticket.getMaVeBan() + ": " + e.getMessage());
            e.printStackTrace(); 
            return null;
        }
    }


    // =================================================================================
    // PHƯƠNG THỨC 2: TẠO HÓA ĐƠN ĐIỀU CHỈNH
    // =================================================================================
    public String createAdjustmentInvoice(TransactionDetail detail, String filePath) {
        if (!checkFont()) return null;

        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        Locale vn = Locale.of("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(vn);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            Paragraph title = new Paragraph("HÓA ĐƠN CHỨNG TỪ ĐIỀU CHỈNH GIAO DỊCH", FONT_TITLE);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("THÔNG TIN ĐIỀU CHỈNH", FONT_HEADER));
            document.add(new Paragraph("----------------------------------------------------------------------------------------------------------------------------------"));

            document.add(new Paragraph(String.format("Mã Giao Dịch Điều Chỉnh: %d", detail.getMaGiaoDich()), FONT_NORMAL));
            document.add(new Paragraph(String.format("Mã Vé Bán Liên Quan: %d", detail.getMaVeBan()), FONT_NORMAL));
            document.add(new Paragraph(String.format("Thời Gian Xử Lý: %s", detail.getThoiGian().toString()), FONT_NORMAL));
            document.add(new Paragraph(String.format("Nhân Viên Xử Lý: %d", detail.getMaNhanVien()), FONT_NORMAL));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("CHI TIẾT TIỀN THU/HOÀN", FONT_HEADER));
            document.add(new Paragraph("----------------------------------------------------------------------------------------------------------------------------------"));
            
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);

            table.addCell(createCell("Loại Nghiệp Vụ", FONT_HEADER, Element.ALIGN_CENTER));
            table.addCell(createCell("Số Tiền", FONT_HEADER, Element.ALIGN_CENTER));

            table.addCell(createCell(translateDbType(detail.getLoaiGiaoDich()), FONT_NORMAL, Element.ALIGN_LEFT));
            
            String formattedAmount = currencyFormatter.format(detail.getSoTienLienQuan());
            table.addCell(createCell(formattedAmount, FONT_MONEY, Element.ALIGN_RIGHT));

            document.add(table);

            String note = detail.getSoTienLienQuan() < 0 ? "Số tiền ÂM: HOÀN TRẢ cho Khách Hàng." : "Số tiền DƯƠNG: THU PHÍ bổ sung.";
            document.add(new Paragraph("\nKết luận: " + note, FONT_HEADER));

            document.close();
            return filePath;

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private boolean checkFont() {
         if (baseFont == null) {
            System.err.println("Không thể tạo hóa đơn do lỗi font đã xảy ra.");
            return false;
        }
        return true;
    }

    private PdfPCell createCell(String content, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        return cell;
    }

    // Helper: Dịch mã CSDL sang Tiếng Việt hiển thị (CHUẨN HÓA)
    private String translateDbType(String dbType) {
        switch (dbType) {
            case "BOOK":
                return "Thanh Toán Đặt Vé";
            case "HUY_VE":
                return "Hoàn Tiền Hủy Vé";
            case "HOAN_DOI":
                return "Điều Chỉnh Giao Dịch Đổi Vé";
            case "THU_PHI": 
                return "Ghi Nhận Thu Phí Bổ Sung";
            case "DIEU_CHINH":
                return "Điều Chỉnh Khác";
            // Mã cũ (giữ lại phòng trường hợp dữ liệu cũ)
            case "CANCEL":
                return "Hoàn Tiền Hủy Vé"; 
            default:
                return dbType;
        }
    }
}
