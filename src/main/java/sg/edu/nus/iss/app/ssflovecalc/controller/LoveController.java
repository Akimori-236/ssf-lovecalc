package sg.edu.nus.iss.app.ssflovecalc.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;
import sg.edu.nus.iss.app.ssflovecalc.model.LoveResult;
import sg.edu.nus.iss.app.ssflovecalc.service.LoveService;

@Controller
@RequestMapping(path = "/lovecalc", produces = MediaType.TEXT_HTML_VALUE)
public class LoveController {

    @Autowired
    private LoveService loveSvc;

    @GetMapping
    public String calculate(@RequestParam(required = true) String fname, @RequestParam(required = true) String sname,
            Model model, HttpServletResponse response) throws IOException {
        LoveResult lr = loveSvc.getCalculation(fname, sname).get();
        if (lr.getPercentage() == 0) {
            response.sendError(418);
            return "teapot";
        }
        model.addAttribute("loveresult", lr);
        return "loveresult";
    }

    @GetMapping(path="/results")
    public String getList(Model model) {
        List<LoveResult> results = loveSvc.getAllResults(0);
        System.out.println(results.get(0));
        model.addAttribute("results", results);
        return "resultslist";
    }
}
