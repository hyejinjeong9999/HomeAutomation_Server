package com.multi.homeAutomation;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import dao.WeatherDAO;
import vo.WeatherVO;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);

		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);

		String formattedDate = dateFormat.format(date);

		model.addAttribute("serverTime", formattedDate);

		return "home";
	}

	@RequestMapping(value = "/getWeather", method = RequestMethod.GET)
	public void getWeather(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		WeatherVO vo = new WeatherVO();
		WeatherDAO dao = new WeatherDAO();
		ArrayList<WeatherVO> result = dao.getWeather(vo);

		response.setContentType("text/plain; charset=utf-8");
		PrintWriter out = response.getWriter();

		ObjectMapper mapper = new ObjectMapper();
		String jsonString = mapper.writeValueAsString(result);
		out.println(jsonString);
		out.flush();
		out.close();

		

	}

}
