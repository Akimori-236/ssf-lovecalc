package sg.edu.nus.iss.app.ssflovecalc.controller;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sg.edu.nus.iss.app.ssflovecalc.model.LoveResult;
import sg.edu.nus.iss.app.ssflovecalc.service.LoveService;

@Controller
@RequestMapping(path = "/lovecalc")
public class CalcController {

    @Autowired
    private LoveService loveSvc;

    @GetMapping
    public String calculate(@RequestParam(required = true) String fname, @RequestParam(required = true) String sname,
            Model model) throws IOException {
        Optional<LoveResult> lr = loveSvc.getCalculation(fname, sname);
        model.addAttribute("loveresult", lr.get());
        return "loveresult";
    }

}
