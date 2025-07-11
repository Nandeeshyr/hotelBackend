package com.hotel.serviceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.pdfbox.io.IOUtils;
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
					fileName = (String) requestMap.get("uuid"); //If isGenerate is false, it means the bill already exists — just regenerate the PDF.
				} else {
					fileName = HotelUtils.getUUID(); //Otherwise, generate a new UUID and insert the bill into the database.
					requestMap.put("uuid", fileName);
					insertBill(requestMap); //InsertBill inserts the bill data inside the DB
				}
				String data = "Name: " + requestMap.get("name") + "\n" + "Contact Number: "
						+ requestMap.get("contactNumber") + "\n" + "Email: " + requestMap.get("email") + "\n"
						+ "Payment Method: " + requestMap.get("paymentMethod") + "\n"; //Builds a string with customer details to include in the PDF.

				Document document = new Document(); //Creates a new PDF document.Sets the output file path using the UUID.
				PdfWriter.getInstance(document,
						new FileOutputStream(HotelConstants.STORE_LOCATION + "\\" + fileName + ".pdf"));

				document.open();
				setRectangleInPdf(document); //Draws a border around the page.

				Paragraph chunk = new Paragraph("Hotel Management System", getFont("Header"));
				chunk.setAlignment(Element.ALIGN_CENTER);
				document.add(chunk); //Adds a centered title.

				Paragraph paragraph = new Paragraph(data + "\n \n", getFont("Data")); 
				document.add(paragraph);	//Adds customer info.

				PdfPTable table = new PdfPTable(5); //Creates a table with 5 columns.
				table.setWidthPercentage(100); 
				addTableHeader(table);

				JSONArray jsonArray = HotelUtils.getJsonArrayFromString((String) requestMap.get("productDetails")); //Parses the productDetails JSON string into a list.
				for (int i = 0; i < jsonArray.length(); i++) { //For each product, converts it to a map and adds a row to the table.
					String itemString = jsonArray.getJSONObject(i).toString();
					Map<String, Object> itemMap = HotelUtils.getMapFromJson(itemString);
					addRows(table, itemMap);
				}

				document.add(table); //Adds the table to the PDF.
				
				Paragraph footer = new Paragraph("Total : " + requestMap.get("totalAmount") + "\n"
						+ "Thank you for visiting. Please visit again !", getFont("Data")); //Adds the total amount and a thank-you message.
				document.add(footer);
				document.close(); //Closes the document.
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

	@Override
	public ResponseEntity<List<Bill>> getBills() {
		List<Bill> list = new ArrayList<>(); //Initializes an empty list that will hold the bill records.
			if(jwtFilter.isAdmin()) {
				list = billDao.getAllBills(); //If admin, fetches all bills from the database using the named query Bill.getAllBills.
			}else {
				list = billDao.getBillByUsername(jwtFilter.getCurrentUser()); //If not admin, fetches only bills created by this user using Bill.getBillByUsername.
			}
			return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) { //This method returns the PDF file (as a byte array) for the given bill UUID.
		logger.info("Inside getPdf: requestMap {}",requestMap);
		try {
			byte[] byteArray = new byte[0];
			if(!requestMap.containsKey("uuid") && validateRequestMap(requestMap)) {
				return new ResponseEntity<>(byteArray,HttpStatus.BAD_REQUEST);
			}
			String filePath = HotelConstants.STORE_LOCATION+"\\"+(String) requestMap.get("uuid") + ".pdf";
			if(HotelUtils.isFileExist(filePath)) {
				byteArray = getByteArray(filePath);
				return new ResponseEntity<>(byteArray,HttpStatus.OK);
			}else {
				requestMap.put("isGenerate", false);
				generateReport(requestMap);
				byteArray = getByteArray(filePath);
				return new ResponseEntity<>(byteArray,HttpStatus.OK);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private byte[] getByteArray(String filePath) throws Exception {
		File initialFile = new File(filePath);
		InputStream targetStream = new FileInputStream(initialFile);
		byte[] byteArray = IOUtils.toByteArray(targetStream);
		targetStream.close();
		return byteArray;
	}

	@Override
	public ResponseEntity<String> deleteBill(Integer id) {
		try {
			Optional optional = billDao.findById(id);
			if(!optional.isEmpty()) {
				billDao.deleteById(id);
				return HotelUtils.getResponseEntity("Bill Deleted Successfully.", HttpStatus.OK);
			}
			return HotelUtils.getResponseEntity("Bill ID does not exist.", HttpStatus.OK);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return HotelUtils.getResponseEntity(HotelConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
