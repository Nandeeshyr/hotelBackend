package com.hotel.serviceImpl;

import java.io.FileOutputStream;

import java.util.Map;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hotel.JWT.JwtFilter;
import com.hotel.POJO.Bill;
import com.hotel.constants.HotelConstants;
import com.hotel.dao.BillDao;
import com.hotel.service.BillService;
import com.hotel.utils.HotelUtils;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BillServiceImpl implements BillService {
	private static final Logger logger = LoggerFactory.getLogger(BillServiceImpl.class);

	@Autowired
	JwtFilter jwtFilter;

	@Autowired
	BillDao billDao;

	@Override
	public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
		logger.info("Inside generateReport");
		try {
			String fileName;
			if (validateRequestMap(requestMap)) {
				if (requestMap.containsKey("isGenerate") && !(Boolean) requestMap.get("isGenerate")) {
					fileName = (String) requestMap.get("uuid");
				} else {
					fileName = HotelUtils.getUUID();
					requestMap.put("uuid", fileName);
					insertBill(requestMap);
				}
				String data = "Name: " + requestMap.get("name") + "\n" + "Contact Number: "
						+ requestMap.get("contactNumber") + "\n" + "Email: " + requestMap.get("email") + "\n"
						+ "Payment Method: " + requestMap.get("paymentMethod") + "\n";

				Document document = new Document();
				PdfWriter.getInstance(document,
						new FileOutputStream(HotelConstants.STORE_LOCATION + "\\" + fileName + ".pdf"));

				document.open();
				setRectangleInPdf(document);

				Paragraph chunk = new Paragraph("Hotel Management System", getFont("Header"));
				chunk.setAlignment(Element.ALIGN_CENTER);
				document.add(chunk);

				Paragraph paragraph = new Paragraph(data + "\n \n", getFont("Data"));
				document.add(paragraph);

				PdfPTable table = new PdfPTable(5);
				table.setWidthPercentage(100);
				addTableHeader(table);

				JSONArray jsonArray = HotelUtils.getJsonArrayFromString((String) requestMap.get("productDetails"));
				for (int i = 0; i < jsonArray.length(); i++) {
					String itemString = jsonArray.getJSONObject(i).toString();
					Map<String, Object> itemMap = HotelUtils.getMapFromJson(itemString);
					addRows(table, itemMap);
				}

				document.add(table);
				Paragraph footer = new Paragraph("Total : " + requestMap.get("totalAmount") + "\n"
						+ "Thank you for visiting. Please visit again !", getFont("Data"));
				document.add(footer);
				document.close();
				return new ResponseEntity<>("{\"uuid\":\"" + fileName + "\"}", HttpStatus.OK);
			}
			return HotelUtils.getResponseEntity("Required data not found.", HttpStatus.BAD_REQUEST);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return HotelUtils.getResponseEntity(HotelConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private void addRows(PdfPTable table, Map<String, Object> data) {
		logger.info("Inside addRows");
		table.addCell((String) data.get("name"));
		table.addCell((String) data.get("category"));
		table.addCell((String) data.get("quantity"));
		table.addCell(Double.toString((Double) data.get("price")));
		table.addCell(Double.toString((Double) data.get("total")));
	}

	private void addTableHeader(PdfPTable table) {
		logger.info("Inside addTableHeader");
		Stream.of("Name", "Category", "Quantity", "Price", "Sub Total").forEach(columnTitle -> {
			PdfPCell header = new PdfPCell();
			header.setBackgroundColor(BaseColor.LIGHT_GRAY);
			header.setBorderWidth(2);
			header.setPhrase(new Phrase(columnTitle));
			header.setBackgroundColor(BaseColor.CYAN);
			header.setHorizontalAlignment(Element.ALIGN_CENTER);
			header.setVerticalAlignment(Element.ALIGN_CENTER);
			table.addCell(header);
		});

	}

	private Font getFont(String type) {
		logger.info("Inside getFont");
		switch (type) {
		case "Header":
			Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, BaseColor.BLUE);
			headerFont.setStyle(Font.BOLD);
			return headerFont;
		case "Data":
			Font dataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 11, BaseColor.GREEN);
		default:
			return new Font();
		}
	}

	private void setRectangleInPdf(Document document) throws DocumentException {
		logger.info("Inside setRectangleInPdf");
		Rectangle rect = new Rectangle(577, 825, 18, 15);
		rect.enableBorderSide(1);
		rect.enableBorderSide(2);
		rect.enableBorderSide(4);
		rect.enableBorderSide(8);
		rect.setBorderColor(BaseColor.ORANGE);
		rect.setBorderWidth(1);
		document.add(rect);
	}

	private void insertBill(Map<String, Object> requestMap) {
		try {
			Bill bill = new Bill();
			bill.setUuid((String) requestMap.get("uuid"));
			bill.setName((String) requestMap.get("name"));
			bill.setEmail((String) requestMap.get("email"));
			bill.setContactNumber((String) requestMap.get("contactNumber"));
			bill.setPaymentMethod((String) requestMap.get("paymentMethod"));
			bill.setTotal(Integer.parseInt((String) requestMap.get("totalAmount")));
			bill.setProductDetails((String) requestMap.get("productDetails"));
			bill.setCreatedBy(jwtFilter.getCurrentUser());
			billDao.save(bill);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private boolean validateRequestMap(Map<String, Object> requestMap) {
		return requestMap.containsKey("name") && requestMap.containsKey("contactNumber")
				&& requestMap.containsKey("email") && requestMap.containsKey("paymentMethod")
				&& requestMap.containsKey("productDetails") && requestMap.containsKey("totalAmount");
	}

}
