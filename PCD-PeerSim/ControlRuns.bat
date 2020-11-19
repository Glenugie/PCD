@cd "C:/Users/Sam/git/PCD/PCD-PeerSim"
@touch "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Control-TrueRandom.txt"
@echo '' > "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Control-TrueRandom.txt" 2>&1
@echo Generating graphs for Control-TrueRandom...
@Rscript "C:/Users/Sam/Dropbox/PhD/ExperimentRes/graphs-new-debug.r" "Control-TrueRandom" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Control-TrueRandom.txt" 2>&1
@cd "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Control-TrueRandom/EPS"
@touch Graphs.tex
@echo \documentclass{article} >> Graphs.tex
@echo \usepackage[utf8]{inputenc} >> Graphs.tex
@echo \usepackage[landscape]{geometry} >> Graphs.tex
@echo \usepackage{graphicx} >> Graphs.tex
@echo \usepackage{epstopdf} >> Graphs.tex
@echo \title{Control-TrueRandom} >> Graphs.tex
@echo \begin{document} >> Graphs.tex
@echo \maketitle >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Control-TrueRandom-messages-sankey.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Control-TrueRandom-messages.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Control-TrueRandom-cycleBreakdown.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Control-TrueRandom-utilScatterAll-Fault.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Control-TrueRandom-utility-final.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \end{document} >> Graphs.tex
pdflatex "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Control-TrueRandom/EPS/Graphs.tex" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Control-TrueRandom.txt" 2>&1
@sleep 1
@rm "Graphs.tex"
@rm "Graphs.aux"
@rm "Graphs.log"
@mv "Graphs.pdf" "C:/Users/Sam/Dropbox/PhD/ExperimentRes/_PDF/Control-TrueRandom.pdf"
@echo ""

@cd "C:/Users/Sam/git/PCD/PCD-PeerSim"
@touch "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Control-NoReason.txt"
@echo '' > "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Control-NoReason.txt" 2>&1
@echo Generating graphs for Control-NoReason...
@Rscript "C:/Users/Sam/Dropbox/PhD/ExperimentRes/graphs-new-debug.r" "Control-NoReason" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Control-NoReason.txt" 2>&1
@cd "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Control-NoReason/EPS"
@touch Graphs.tex
@echo \documentclass{article} >> Graphs.tex
@echo \usepackage[utf8]{inputenc} >> Graphs.tex
@echo \usepackage[landscape]{geometry} >> Graphs.tex
@echo \usepackage{graphicx} >> Graphs.tex
@echo \usepackage{epstopdf} >> Graphs.tex
@echo \title{Control-NoReason} >> Graphs.tex
@echo \begin{document} >> Graphs.tex
@echo \maketitle >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Control-NoReason-messages-sankey.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Control-NoReason-messages.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Control-NoReason-cycleBreakdown.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Control-NoReason-utilScatterAll-Fault.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Control-NoReason-utility-final.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \end{document} >> Graphs.tex
pdflatex "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Control-NoReason/EPS/Graphs.tex" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Control-NoReason.txt" 2>&1
@sleep 1
@rm "Graphs.tex"
@rm "Graphs.aux"
@rm "Graphs.log"
@mv "Graphs.pdf" "C:/Users/Sam/Dropbox/PhD/ExperimentRes/_PDF/Control-NoReason.pdf"
@echo ""