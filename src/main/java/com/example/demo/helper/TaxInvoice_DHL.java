package com.example.demo.helper;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.vandeseer.easytable.TableDrawer;
import org.vandeseer.easytable.settings.HorizontalAlignment;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.Table.TableBuilder;
import org.vandeseer.easytable.structure.cell.TextCell;

public class TaxInvoice_DHL {
	private PDDocument document;
	private PDPage page;
	private PDPageContentStream contentStream;
	private static final PDRectangle PAGE_SIZE = PDRectangle.A4;
	private static final float MARGIN = 30;
	private static float y = PAGE_SIZE.getHeight() - 60;
	private static float tmp = 0;
	private static final PDFont TEXT_FONT = PDType1Font.TIMES_ROMAN;
	private static final PDFont TEXT_FONT_BOLD = PDType1Font.TIMES_BOLD;

	private static final int FONT_SIZE = 10;
	private static final float padding = 6;

	public TaxInvoice_DHL() {
		document = new PDDocument();
	}

	public void export(HttpServletResponse response) throws IOException {
		page = new PDPage();
		document.addPage(page);
		contentStream = new PDPageContentStream(document, page);
		contentStream.setFont(TEXT_FONT, FONT_SIZE);

		// 1: insert logo
		insertImage("logo.png", 0, y);
		y -= 20;
		tmp = y;

		// 2: insert original
		drawMultiLineText("ORIGINAL", PAGE_SIZE.getWidth() / 2 + 130 + MARGIN * 2, y, 100, page, contentStream,
				TEXT_FONT_BOLD, FONT_SIZE + 4, padding);
		y = tmp;

		// 3: insert content 1
		y = drawMultiLineText("DHL Distribution (Thai Lan) Limited (Branch No. 00005)", MARGIN, y,
				PAGE_SIZE.getWidth() / 2 + 150, page, contentStream, TEXT_FONT_BOLD, FONT_SIZE + 2, padding);

		y = drawMultiLineText("93/1, 2nd Floor, GPF Witthayu Towers Witthayu Road, Lumpini, Pathumwan, Bangkok 10330",
				MARGIN, y, PAGE_SIZE.getWidth() / 2 - 150, page, contentStream, TEXT_FONT, FONT_SIZE, padding);
		y = drawMultiLineText("TAX ID NO. 0105535099502", MARGIN, y, PAGE_SIZE.getWidth() / 2 - 100, page,
				contentStream, TEXT_FONT, FONT_SIZE, padding);

		// insert table tax invoices
		drawTable(PAGE_SIZE.getWidth() / 2, y, createTableTaxInvoice());

		// insert content 2
		tmp = drawMultiLineText("Jaspal Company Limited", MARGIN, tmp, PAGE_SIZE.getWidth() / 2 - 100, page,
				contentStream, TEXT_FONT_BOLD, FONT_SIZE, padding);

		tmp = drawMultiLineText("(Head Office)TAX ID: 0105530052354", MARGIN, tmp, PAGE_SIZE.getWidth() / 2 - 100, page,
				contentStream, TEXT_FONT_BOLD, FONT_SIZE, padding);
		drawMultiLineText("1054 Soi Sukhumvit 66/1, Sukhumvit Rd, Prakanongtai, Prakanong, Bangkok 10260", MARGIN, tmp,
				PAGE_SIZE.getWidth() / 2 - 100, page, contentStream, TEXT_FONT_BOLD, FONT_SIZE, padding);
		// insert table SUMMARY DESCRIPTION
		drawTable(MARGIN, y, createTableSummary());

		// insert sign
		insertImage("sign.PNG", 0, y);
		// insert footer
		insertFooter();
		contentStream.close();
		document.save(response.getOutputStream());
		document.close();
	}

	private void insertFooter() throws IOException {
		PDImageXObject pdImage = PDImageXObject.createFromFile("footer.PNG", document);
		
		contentStream.drawImage(pdImage,0, 50);

		
	}

	private Table createTableSummary() {
		TableBuilder table = Table.builder()
				.addColumnsOfWidth((PAGE_SIZE.getWidth() - MARGIN * 2) / 3 * 2, (PAGE_SIZE.getWidth() - MARGIN * 2) / 3)
				.fontSize(FONT_SIZE).font(TEXT_FONT);

		table.addRow(Row.builder()
				.add(TextCell.builder().borderWidthTop(0.5f).borderWidthLeft(0.5f).borderWidthBottom(0.5f)
						.borderWidthRight(0.5f).font(TEXT_FONT_BOLD).text("SUMMARY DESCRIPTION").build())
				.add(TextCell.builder().horizontalAlignment(HorizontalAlignment.RIGHT).text("Amount (BAHT)")
						.borderWidthRight(0.5f).font(TEXT_FONT_BOLD).borderWidthTop(0.5f).borderWidthBottom(0.5f)
						.build())
				.build());
		y -= (FONT_SIZE + padding);
		table.addRow(
				Row.builder().add(TextCell.builder().borderWidthLeft(0.5f).borderWidthRight(0.5f).text(" ").build())
						.add(TextCell.builder().horizontalAlignment(HorizontalAlignment.RIGHT).text(" ")
								.borderWidthRight(0.5f).build())
						.build());
		y -= (FONT_SIZE + padding);
		table.addRow(Row.builder()
				.add(TextCell.builder().borderWidthLeft(0.5f).paddingLeft(30).borderWidthRight(0.5f).text("Value of Service : COD Fee")
						.build())
				.add(TextCell.builder().horizontalAlignment(HorizontalAlignment.RIGHT).text("804,20")
						.borderWidthRight(0.5f).build())
				.build());
		y -= (FONT_SIZE + padding);
		table.addRow(Row.builder()
				.add(TextCell.builder().borderWidthLeft(0.5f).paddingLeft(30).borderWidthRight(0.5f).text("Output Tax 7%").build())
				.add(TextCell.builder().horizontalAlignment(HorizontalAlignment.RIGHT).text("56,29")
						.borderWidthRight(0.5f).build())
				.build());
		y -= (FONT_SIZE + padding);
		for (int i = 0; i < 10; i++) {
			table.addRow(
					Row.builder().add(TextCell.builder().borderWidthLeft(0.5f).borderWidthRight(0.5f).text(" ").build())
							.add(TextCell.builder().horizontalAlignment(HorizontalAlignment.RIGHT).text(" ")
									.borderWidthRight(0.5f).build())
							.build());
			y -= (FONT_SIZE + padding);
		}
		table.addRow(Row.builder()
				.add(TextCell.builder().borderWidthTop(0.5f).borderWidthLeft(0.5f).borderWidthBottom(0.5f)
						.borderWidthTop(0.5f).font(TEXT_FONT_BOLD).borderWidthRight(0.5f).text("Total Amount Include VAT").build())
				.add(TextCell.builder().horizontalAlignment(HorizontalAlignment.RIGHT).text("860,49")
						.borderWidthBottom(0.5f).font(TEXT_FONT_BOLD).borderWidthTop(0.5f).borderWidthRight(0.5f).build())
				.build());
		y -= (FONT_SIZE + padding);

		return table.build();
	}

	public void drawTable(float startX, float startY, Table table) {
		TableDrawer.builder().contentStream(contentStream).table(table).startX(startX).startY(startY).build().draw();
	}

	private static Table createTableTaxInvoice() {

		TableBuilder table = Table.builder()
				.addColumnsOfWidth((PAGE_SIZE.getWidth() / 2 - MARGIN) / 2, (PAGE_SIZE.getWidth() / 2 - MARGIN) / 2)
				.fontSize(FONT_SIZE).font(TEXT_FONT);
		table.addRow(Row.builder()
				.add(TextCell.builder().backgroundColor(Color.LIGHT_GRAY).borderWidthTop(0.5f).borderWidthLeft(0.5f)
						.fontSize(FONT_SIZE + 4).font(TEXT_FONT_BOLD).text("Tax Invoice").build())
				.add(TextCell.builder().text("").backgroundColor(Color.LIGHT_GRAY).borderWidthRight(0.5f)
						.borderWidthTop(0.5f).build())
				.build());

		y -= (FONT_SIZE + 4 + padding);

		tmp = y;
		table.addRow(
				Row.builder().add(TextCell.builder().font(TEXT_FONT_BOLD).text("Number").borderWidthLeft(0.5f).build())
						.add(TextCell.builder().text("2300088764").borderWidthRight(0.5f).build()).build());
		y -= (FONT_SIZE + padding);
		table.addRow(Row.builder()
				.add(TextCell.builder().font(TEXT_FONT_BOLD).text("Reference No.").borderWidthLeft(0.5f).build())
				.add(TextCell.builder().text("RDS20100006").borderWidthRight(0.5f).build()).build());
		y -= (FONT_SIZE + padding);
		table.addRow(Row.builder()
				.add(TextCell.builder().font(TEXT_FONT_BOLD).text("Customer Account").borderWidthLeft(0.5f).build())
				.add(TextCell.builder().text("5257308831").borderWidthRight(0.5f).build()).build());
		y -= (FONT_SIZE + padding);
		table.addRow(
				Row.builder().add(TextCell.builder().font(TEXT_FONT_BOLD).text("Date").borderWidthLeft(0.5f).build())
						.add(TextCell.builder().text("02.10.2020").borderWidthRight(0.5f).build()).build());
		y -= (FONT_SIZE + padding);
		table.addRow(Row.builder()
				.add(TextCell.builder().text("").borderWidthLeft(0.5f).borderWidthBottom(0.5f).build())
				.add(TextCell.builder().text("").borderWidthRight(0.5f).borderWidthBottom(0.5f).build()).build());
		y -= (FONT_SIZE + padding);
		return table.build();
	}

	private void insertImage(String path, float px, float py) throws IOException {
		PDImageXObject pdImage = PDImageXObject.createFromFile(path, document);
		y -= pdImage.getHeight();
		contentStream.drawImage(pdImage, PAGE_SIZE.getWidth() - pdImage.getWidth() - MARGIN, y);

	}

	private float drawMultiLineText(String text, float x, float py, float allowedWidth, PDPage page,
			PDPageContentStream contentStream, PDFont font, float fontSize, float lineHeight) throws IOException {

		List<String> lines = new ArrayList<String>();

		String myLine = "";
		String[] words = text.split(" ");
		for (String word : words) {

			if (!myLine.isEmpty()) {
				myLine += " ";
			}

			int size = (int) (fontSize * font.getStringWidth(myLine + word) / 1000);
			if (size > allowedWidth) {
				lines.add(myLine);

				myLine = word;
			} else {
				myLine += word;
			}
		}
		lines.add(myLine);

		for (String line : lines) {
			contentStream.beginText();
			contentStream.setFont(font, fontSize);
			contentStream.newLineAtOffset(x, py);
			contentStream.showText(line);
			contentStream.endText();

			py -= (lineHeight + fontSize);
		}
		return py;
	}
}
