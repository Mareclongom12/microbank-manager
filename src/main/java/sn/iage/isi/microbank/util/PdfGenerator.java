package sn.iage.isi.microbank.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import sn.iage.isi.microbank.model.Account;
import sn.iage.isi.microbank.model.Operation;
import sn.iage.isi.microbank.model.OperationType;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PdfGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
    private static final Font BOLD_FONT = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);

    public byte[] generateStatement(Account account, List<Operation> operations,
                                    String periodeDebut, String periodeFin) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter.getInstance(document, out);
            document.open();

            // ===== En-tête =====
            Paragraph title = new Paragraph("MICROBANK", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("RELEVÉ DE COMPTE",
                    new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD));
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(20);
            document.add(subtitle);

            // ===== Informations client / compte =====
            document.add(new Paragraph("Client : " + account.getClient().getPrenom()
                    + " " + account.getClient().getNom(), NORMAL_FONT));
            document.add(new Paragraph("Compte : " + account.getNumeroCompte(), NORMAL_FONT));
            document.add(new Paragraph("Type : " + account.getType(), NORMAL_FONT));
            document.add(new Paragraph("Période : " + periodeDebut + " - " + periodeFin, NORMAL_FONT));
            document.add(Chunk.NEWLINE);

            // ===== Tableau des opérations =====
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2, 2, 3, 2});

            addHeaderCell(table, "Date");
            addHeaderCell(table, "Type");
            addHeaderCell(table, "Description");
            addHeaderCell(table, "Montant");

            BigDecimal totalDepots = BigDecimal.ZERO;
            BigDecimal totalRetraits = BigDecimal.ZERO;

            for (Operation op : operations) {
                table.addCell(new PdfPCell(new Phrase(
                        op.getDateOperation().format(DATE_FORMAT), NORMAL_FONT)));
                table.addCell(new PdfPCell(new Phrase(op.getType().toString(), NORMAL_FONT)));
                table.addCell(new PdfPCell(new Phrase(
                        op.getDescription() != null ? op.getDescription() : "-", NORMAL_FONT)));

                String signe = op.getType() == OperationType.RETRAIT ? "-" : "+";
                table.addCell(new PdfPCell(new Phrase(
                        signe + " " + op.getMontant() + " FCFA", NORMAL_FONT)));

                if (op.getType() == OperationType.DEPOT) {
                    totalDepots = totalDepots.add(op.getMontant());
                } else if (op.getType() == OperationType.RETRAIT) {
                    totalRetraits = totalRetraits.add(op.getMontant());
                } else if (op.getType() == OperationType.VIREMENT) {
                    // Un virement est un débit du point de vue du compte source
                    totalRetraits = totalRetraits.add(op.getMontant());
                }
            }

            if (operations.isEmpty()) {
                PdfPCell emptyCell = new PdfPCell(new Phrase("Aucune opération sur cette période", NORMAL_FONT));
                emptyCell.setColspan(4);
                emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(emptyCell);
            }

            document.add(table);
            document.add(Chunk.NEWLINE);

            // ===== Totaux =====
            document.add(new Paragraph("Total des dépôts : " + totalDepots + " FCFA", BOLD_FONT));
            document.add(new Paragraph("Total des retraits/virements sortants : " + totalRetraits + " FCFA", BOLD_FONT));
            document.add(Chunk.NEWLINE);

            Paragraph soldeFinal = new Paragraph("Solde final : " + account.getSolde() + " FCFA",
                    new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD));
            document.add(soldeFinal);

            document.close();
            return out.toByteArray();

        } catch (DocumentException e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }

    private void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, HEADER_FONT));
        cell.setBackgroundColor(new BaseColor(33, 37, 41)); // couleur proche du bg-dark Bootstrap
        cell.setPadding(6);
        table.addCell(cell);
    }
}