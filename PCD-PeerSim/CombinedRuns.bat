@cd "C:/Users/Sam/git/PCD/PCD-PeerSim"
@echo Generating graphs for Pseu,90,90,1-10O-100N...
@Rscript "C:/Users/Sam/Dropbox/PhD/ExperimentRes/graphs-new-debug.r" "Pseu,90,90,1-10O-100N" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Pseu,90,90,1-10O-100N.txt" 2>&1
@cd "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Pseu,90,90,1-10O-100N/EPS"
@touch Graphs.tex
@echo \documentclass{article} >> Graphs.tex
@echo \usepackage[utf8]{inputenc} >> Graphs.tex
@echo \usepackage[landscape]{geometry} >> Graphs.tex
@echo \usepackage{graphicx} >> Graphs.tex
@echo \usepackage{epstopdf} >> Graphs.tex
@echo \title{Pseu,90,90,1-10O-100N} >> Graphs.tex
@echo \begin{document} >> Graphs.tex
@echo \maketitle >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Pseu-90-90-1-10O-100N-messages-sankey.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Pseu-90-90-1-10O-100N-messages.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Pseu-90-90-1-10O-100N-cycleBreakdown.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Pseu-90-90-1-10O-100N-utilScatterAll-Fault.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Pseu-90-90-1-10O-100N-utility-final.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \end{document} >> Graphs.tex
pdflatex "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Pseu,90,90,1-10O-100N/EPS/Graphs.tex" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Pseu,90,90,1-10O-100N.txt" 2>&1
@sleep 1
@rm "Graphs.tex"
@rm "Graphs.aux"
@rm "Graphs.log"
@mv "Graphs.pdf" "C:/Users/Sam/Dropbox/PhD/ExperimentRes/_PDF/Pseu-90-90-1-10O-100N.pdf"
@echo ""

@cd "C:/Users/Sam/git/PCD/PCD-PeerSim"
@echo Generating graphs for Pseu,90,90,5-25O-100N...
@Rscript "C:/Users/Sam/Dropbox/PhD/ExperimentRes/graphs-new-debug.r" "Pseu,90,90,5-25O-100N" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Pseu,90,90,5-25O-100N.txt" 2>&1
@cd "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Pseu,90,90,5-25O-100N/EPS"
@touch Graphs.tex
@echo \documentclass{article} >> Graphs.tex
@echo \usepackage[utf8]{inputenc} >> Graphs.tex
@echo \usepackage[landscape]{geometry} >> Graphs.tex
@echo \usepackage{graphicx} >> Graphs.tex
@echo \usepackage{epstopdf} >> Graphs.tex
@echo \title{Pseu,90,90,5-25O-100N} >> Graphs.tex
@echo \begin{document} >> Graphs.tex
@echo \maketitle >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Pseu-90-90-5-25O-100N-messages-sankey.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Pseu-90-90-5-25O-100N-messages.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Pseu-90-90-5-25O-100N-cycleBreakdown.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Pseu-90-90-5-25O-100N-utilScatterAll-Fault.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Pseu-90-90-5-25O-100N-utility-final.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \end{document} >> Graphs.tex
pdflatex "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Pseu,90,90,5-25O-100N/EPS/Graphs.tex" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Pseu,90,90,5-25O-100N.txt" 2>&1
@sleep 1
@rm "Graphs.tex"
@rm "Graphs.aux"
@rm "Graphs.log"
@mv "Graphs.pdf" "C:/Users/Sam/Dropbox/PhD/ExperimentRes/_PDF/Pseu-90-90-5-25O-100N.pdf"
@echo ""

@cd "C:/Users/Sam/git/PCD/PCD-PeerSim"
@echo Generating graphs for Pseu,90,90,10-50O-50N...
@Rscript "C:/Users/Sam/Dropbox/PhD/ExperimentRes/graphs-new-debug.r" "Pseu,90,90,10-50O-50N" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Pseu,90,90,10-50O-50N.txt" 2>&1
@cd "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Pseu,90,90,10-50O-50N/EPS"
@touch Graphs.tex
@echo \documentclass{article} >> Graphs.tex
@echo \usepackage[utf8]{inputenc} >> Graphs.tex
@echo \usepackage[landscape]{geometry} >> Graphs.tex
@echo \usepackage{graphicx} >> Graphs.tex
@echo \usepackage{epstopdf} >> Graphs.tex
@echo \title{Pseu,90,90,10-50O-50N} >> Graphs.tex
@echo \begin{document} >> Graphs.tex
@echo \maketitle >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Pseu-90-90-10-50O-50N-messages-sankey.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Pseu-90-90-10-50O-50N-messages.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Pseu-90-90-10-50O-50N-cycleBreakdown.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Pseu-90-90-10-50O-50N-utilScatterAll-Fault.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Pseu-90-90-10-50O-50N-utility-final.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \end{document} >> Graphs.tex
pdflatex "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Pseu,90,90,10-50O-50N/EPS/Graphs.tex" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Pseu,90,90,10-50O-50N.txt" 2>&1
@sleep 1
@rm "Graphs.tex"
@rm "Graphs.aux"
@rm "Graphs.log"
@mv "Graphs.pdf" "C:/Users/Sam/Dropbox/PhD/ExperimentRes/_PDF/Pseu-90-90-10-50O-50N.pdf"
@echo ""

@cd "C:/Users/Sam/git/PCD/PCD-PeerSim"
@echo Generating graphs for Ring,50,50,1-10O-100N...
@Rscript "C:/Users/Sam/Dropbox/PhD/ExperimentRes/graphs-new-debug.r" "Ring,50,50,1-10O-100N" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,50,50,1-10O-100N.txt" 2>&1
@cd "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,50,50,1-10O-100N/EPS"
@touch Graphs.tex
@echo \documentclass{article} >> Graphs.tex
@echo \usepackage[utf8]{inputenc} >> Graphs.tex
@echo \usepackage[landscape]{geometry} >> Graphs.tex
@echo \usepackage{graphicx} >> Graphs.tex
@echo \usepackage{epstopdf} >> Graphs.tex
@echo \title{Ring,50,50,1-10O-100N} >> Graphs.tex
@echo \begin{document} >> Graphs.tex
@echo \maketitle >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-50-1-10O-100N-messages-sankey.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-50-1-10O-100N-messages.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-50-1-10O-100N-cycleBreakdown.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-50-1-10O-100N-utilScatterAll-Fault.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-50-1-10O-100N-utility-final.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \end{document} >> Graphs.tex
pdflatex "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,50,50,1-10O-100N/EPS/Graphs.tex" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,50,50,1-10O-100N.txt" 2>&1
@sleep 1
@rm "Graphs.tex"
@rm "Graphs.aux"
@rm "Graphs.log"
@mv "Graphs.pdf" "C:/Users/Sam/Dropbox/PhD/ExperimentRes/_PDF/Ring-50-50-1-10O-100N.pdf"
@echo ""

@cd "C:/Users/Sam/git/PCD/PCD-PeerSim"
@echo Generating graphs for Ring,50,50,5-25O-100N...
@Rscript "C:/Users/Sam/Dropbox/PhD/ExperimentRes/graphs-new-debug.r" "Ring,50,50,5-25O-100N" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,50,50,5-25O-100N.txt" 2>&1
@cd "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,50,50,5-25O-100N/EPS"
@touch Graphs.tex
@echo \documentclass{article} >> Graphs.tex
@echo \usepackage[utf8]{inputenc} >> Graphs.tex
@echo \usepackage[landscape]{geometry} >> Graphs.tex
@echo \usepackage{graphicx} >> Graphs.tex
@echo \usepackage{epstopdf} >> Graphs.tex
@echo \title{Ring,50,50,5-25O-100N} >> Graphs.tex
@echo \begin{document} >> Graphs.tex
@echo \maketitle >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-50-5-25O-100N-messages-sankey.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-50-5-25O-100N-messages.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-50-5-25O-100N-cycleBreakdown.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-50-5-25O-100N-utilScatterAll-Fault.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-50-5-25O-100N-utility-final.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \end{document} >> Graphs.tex
pdflatex "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,50,50,5-25O-100N/EPS/Graphs.tex" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,50,50,5-25O-100N.txt" 2>&1
@sleep 1
@rm "Graphs.tex"
@rm "Graphs.aux"
@rm "Graphs.log"
@mv "Graphs.pdf" "C:/Users/Sam/Dropbox/PhD/ExperimentRes/_PDF/Ring-50-50-5-25O-100N.pdf"
@echo ""

@cd "C:/Users/Sam/git/PCD/PCD-PeerSim"
@echo Generating graphs for Ring,50,50,10-50O-50N...
@Rscript "C:/Users/Sam/Dropbox/PhD/ExperimentRes/graphs-new-debug.r" "Ring,50,50,10-50O-50N" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,50,50,10-50O-50N.txt" 2>&1
@cd "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,50,50,10-50O-50N/EPS"
@touch Graphs.tex
@echo \documentclass{article} >> Graphs.tex
@echo \usepackage[utf8]{inputenc} >> Graphs.tex
@echo \usepackage[landscape]{geometry} >> Graphs.tex
@echo \usepackage{graphicx} >> Graphs.tex
@echo \usepackage{epstopdf} >> Graphs.tex
@echo \title{Ring,50,50,10-50O-50N} >> Graphs.tex
@echo \begin{document} >> Graphs.tex
@echo \maketitle >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-50-10-50O-50N-messages-sankey.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-50-10-50O-50N-messages.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-50-10-50O-50N-cycleBreakdown.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-50-10-50O-50N-utilScatterAll-Fault.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-50-10-50O-50N-utility-final.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \end{document} >> Graphs.tex
pdflatex "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,50,50,10-50O-50N/EPS/Graphs.tex" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,50,50,10-50O-50N.txt" 2>&1
@sleep 1
@rm "Graphs.tex"
@rm "Graphs.aux"
@rm "Graphs.log"
@mv "Graphs.pdf" "C:/Users/Sam/Dropbox/PhD/ExperimentRes/_PDF/Ring-50-50-10-50O-50N.pdf"
@echo ""

@cd "C:/Users/Sam/git/PCD/PCD-PeerSim"
@echo Generating graphs for Ring,50,10,1-10O-100N...
@Rscript "C:/Users/Sam/Dropbox/PhD/ExperimentRes/graphs-new-debug.r" "Ring,50,10,1-10O-100N" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,50,10,1-10O-100N.txt" 2>&1
@cd "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,50,10,1-10O-100N/EPS"
@touch Graphs.tex
@echo \documentclass{article} >> Graphs.tex
@echo \usepackage[utf8]{inputenc} >> Graphs.tex
@echo \usepackage[landscape]{geometry} >> Graphs.tex
@echo \usepackage{graphicx} >> Graphs.tex
@echo \usepackage{epstopdf} >> Graphs.tex
@echo \title{Ring,50,10,1-10O-100N} >> Graphs.tex
@echo \begin{document} >> Graphs.tex
@echo \maketitle >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-10-1-10O-100N-messages-sankey.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-10-1-10O-100N-messages.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-10-1-10O-100N-cycleBreakdown.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-10-1-10O-100N-utilScatterAll-Fault.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-10-1-10O-100N-utility-final.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \end{document} >> Graphs.tex
pdflatex "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,50,10,1-10O-100N/EPS/Graphs.tex" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,50,10,1-10O-100N.txt" 2>&1
@sleep 1
@rm "Graphs.tex"
@rm "Graphs.aux"
@rm "Graphs.log"
@mv "Graphs.pdf" "C:/Users/Sam/Dropbox/PhD/ExperimentRes/_PDF/Ring-50-10-1-10O-100N.pdf"
@echo ""

@cd "C:/Users/Sam/git/PCD/PCD-PeerSim"
@echo Generating graphs for Ring,50,10,5-25O-100N...
@Rscript "C:/Users/Sam/Dropbox/PhD/ExperimentRes/graphs-new-debug.r" "Ring,50,10,5-25O-100N" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,50,10,5-25O-100N.txt" 2>&1
@cd "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,50,10,5-25O-100N/EPS"
@touch Graphs.tex
@echo \documentclass{article} >> Graphs.tex
@echo \usepackage[utf8]{inputenc} >> Graphs.tex
@echo \usepackage[landscape]{geometry} >> Graphs.tex
@echo \usepackage{graphicx} >> Graphs.tex
@echo \usepackage{epstopdf} >> Graphs.tex
@echo \title{Ring,50,10,5-25O-100N} >> Graphs.tex
@echo \begin{document} >> Graphs.tex
@echo \maketitle >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-10-5-25O-100N-messages-sankey.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-10-5-25O-100N-messages.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-10-5-25O-100N-cycleBreakdown.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-10-5-25O-100N-utilScatterAll-Fault.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-10-5-25O-100N-utility-final.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \end{document} >> Graphs.tex
pdflatex "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,50,10,5-25O-100N/EPS/Graphs.tex" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,50,10,5-25O-100N.txt" 2>&1
@sleep 1
@rm "Graphs.tex"
@rm "Graphs.aux"
@rm "Graphs.log"
@mv "Graphs.pdf" "C:/Users/Sam/Dropbox/PhD/ExperimentRes/_PDF/Ring-50-10-5-25O-100N.pdf"
@echo ""

@cd "C:/Users/Sam/git/PCD/PCD-PeerSim"
@echo Generating graphs for Ring,50,10,10-50O-50N...
@Rscript "C:/Users/Sam/Dropbox/PhD/ExperimentRes/graphs-new-debug.r" "Ring,50,10,10-50O-50N" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,50,10,10-50O-50N.txt" 2>&1
@cd "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,50,10,10-50O-50N/EPS"
@touch Graphs.tex
@echo \documentclass{article} >> Graphs.tex
@echo \usepackage[utf8]{inputenc} >> Graphs.tex
@echo \usepackage[landscape]{geometry} >> Graphs.tex
@echo \usepackage{graphicx} >> Graphs.tex
@echo \usepackage{epstopdf} >> Graphs.tex
@echo \title{Ring,50,10,10-50O-50N} >> Graphs.tex
@echo \begin{document} >> Graphs.tex
@echo \maketitle >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-10-10-50O-50N-messages-sankey.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-10-10-50O-50N-messages.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-10-10-50O-50N-cycleBreakdown.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-10-10-50O-50N-utilScatterAll-Fault.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-10-10-50O-50N-utility-final.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \end{document} >> Graphs.tex
pdflatex "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,50,10,10-50O-50N/EPS/Graphs.tex" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,50,10,10-50O-50N.txt" 2>&1
@sleep 1
@rm "Graphs.tex"
@rm "Graphs.aux"
@rm "Graphs.log"
@mv "Graphs.pdf" "C:/Users/Sam/Dropbox/PhD/ExperimentRes/_PDF/Ring-50-10-10-50O-50N.pdf"
@echo ""

@cd "C:/Users/Sam/git/PCD/PCD-PeerSim"
@echo Generating graphs for Ring,50,90,1-10O-100N...
@Rscript "C:/Users/Sam/Dropbox/PhD/ExperimentRes/graphs-new-debug.r" "Ring,50,90,1-10O-100N" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,50,90,1-10O-100N.txt" 2>&1
@cd "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,50,90,1-10O-100N/EPS"
@touch Graphs.tex
@echo \documentclass{article} >> Graphs.tex
@echo \usepackage[utf8]{inputenc} >> Graphs.tex
@echo \usepackage[landscape]{geometry} >> Graphs.tex
@echo \usepackage{graphicx} >> Graphs.tex
@echo \usepackage{epstopdf} >> Graphs.tex
@echo \title{Ring,50,90,1-10O-100N} >> Graphs.tex
@echo \begin{document} >> Graphs.tex
@echo \maketitle >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-90-1-10O-100N-messages-sankey.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-90-1-10O-100N-messages.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-90-1-10O-100N-cycleBreakdown.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-90-1-10O-100N-utilScatterAll-Fault.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-90-1-10O-100N-utility-final.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \end{document} >> Graphs.tex
pdflatex "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,50,90,1-10O-100N/EPS/Graphs.tex" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,50,90,1-10O-100N.txt" 2>&1
@sleep 1
@rm "Graphs.tex"
@rm "Graphs.aux"
@rm "Graphs.log"
@mv "Graphs.pdf" "C:/Users/Sam/Dropbox/PhD/ExperimentRes/_PDF/Ring-50-90-1-10O-100N.pdf"
@echo ""

@cd "C:/Users/Sam/git/PCD/PCD-PeerSim"
@echo Generating graphs for Ring,50,90,5-25O-100N...
@Rscript "C:/Users/Sam/Dropbox/PhD/ExperimentRes/graphs-new-debug.r" "Ring,50,90,5-25O-100N" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,50,90,5-25O-100N.txt" 2>&1
@cd "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,50,90,5-25O-100N/EPS"
@touch Graphs.tex
@echo \documentclass{article} >> Graphs.tex
@echo \usepackage[utf8]{inputenc} >> Graphs.tex
@echo \usepackage[landscape]{geometry} >> Graphs.tex
@echo \usepackage{graphicx} >> Graphs.tex
@echo \usepackage{epstopdf} >> Graphs.tex
@echo \title{Ring,50,90,5-25O-100N} >> Graphs.tex
@echo \begin{document} >> Graphs.tex
@echo \maketitle >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-90-5-25O-100N-messages-sankey.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-90-5-25O-100N-messages.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-90-5-25O-100N-cycleBreakdown.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-90-5-25O-100N-utilScatterAll-Fault.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-90-5-25O-100N-utility-final.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \end{document} >> Graphs.tex
pdflatex "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,50,90,5-25O-100N/EPS/Graphs.tex" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,50,90,5-25O-100N.txt" 2>&1
@sleep 1
@rm "Graphs.tex"
@rm "Graphs.aux"
@rm "Graphs.log"
@mv "Graphs.pdf" "C:/Users/Sam/Dropbox/PhD/ExperimentRes/_PDF/Ring-50-90-5-25O-100N.pdf"
@echo ""

@cd "C:/Users/Sam/git/PCD/PCD-PeerSim"
@echo Generating graphs for Ring,50,90,10-50O-50N...
@Rscript "C:/Users/Sam/Dropbox/PhD/ExperimentRes/graphs-new-debug.r" "Ring,50,90,10-50O-50N" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,50,90,10-50O-50N.txt" 2>&1
@cd "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,50,90,10-50O-50N/EPS"
@touch Graphs.tex
@echo \documentclass{article} >> Graphs.tex
@echo \usepackage[utf8]{inputenc} >> Graphs.tex
@echo \usepackage[landscape]{geometry} >> Graphs.tex
@echo \usepackage{graphicx} >> Graphs.tex
@echo \usepackage{epstopdf} >> Graphs.tex
@echo \title{Ring,50,90,10-50O-50N} >> Graphs.tex
@echo \begin{document} >> Graphs.tex
@echo \maketitle >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-90-10-50O-50N-messages-sankey.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-90-10-50O-50N-messages.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-90-10-50O-50N-cycleBreakdown.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-90-10-50O-50N-utilScatterAll-Fault.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-50-90-10-50O-50N-utility-final.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \end{document} >> Graphs.tex
pdflatex "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,50,90,10-50O-50N/EPS/Graphs.tex" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,50,90,10-50O-50N.txt" 2>&1
@sleep 1
@rm "Graphs.tex"
@rm "Graphs.aux"
@rm "Graphs.log"
@mv "Graphs.pdf" "C:/Users/Sam/Dropbox/PhD/ExperimentRes/_PDF/Ring-50-90-10-50O-50N.pdf"
@echo ""

@cd "C:/Users/Sam/git/PCD/PCD-PeerSim"
@echo Generating graphs for Ring,10,50,1-10O-100N...
@Rscript "C:/Users/Sam/Dropbox/PhD/ExperimentRes/graphs-new-debug.r" "Ring,10,50,1-10O-100N" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,10,50,1-10O-100N.txt" 2>&1
@cd "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,10,50,1-10O-100N/EPS"
@touch Graphs.tex
@echo \documentclass{article} >> Graphs.tex
@echo \usepackage[utf8]{inputenc} >> Graphs.tex
@echo \usepackage[landscape]{geometry} >> Graphs.tex
@echo \usepackage{graphicx} >> Graphs.tex
@echo \usepackage{epstopdf} >> Graphs.tex
@echo \title{Ring,10,50,1-10O-100N} >> Graphs.tex
@echo \begin{document} >> Graphs.tex
@echo \maketitle >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-50-1-10O-100N-messages-sankey.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-50-1-10O-100N-messages.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-50-1-10O-100N-cycleBreakdown.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-50-1-10O-100N-utilScatterAll-Fault.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-50-1-10O-100N-utility-final.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \end{document} >> Graphs.tex
pdflatex "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,10,50,1-10O-100N/EPS/Graphs.tex" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,10,50,1-10O-100N.txt" 2>&1
@sleep 1
@rm "Graphs.tex"
@rm "Graphs.aux"
@rm "Graphs.log"
@mv "Graphs.pdf" "C:/Users/Sam/Dropbox/PhD/ExperimentRes/_PDF/Ring-10-50-1-10O-100N.pdf"
@echo ""

@cd "C:/Users/Sam/git/PCD/PCD-PeerSim"
@echo Generating graphs for Ring,10,50,5-25O-100N...
@Rscript "C:/Users/Sam/Dropbox/PhD/ExperimentRes/graphs-new-debug.r" "Ring,10,50,5-25O-100N" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,10,50,5-25O-100N.txt" 2>&1
@cd "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,10,50,5-25O-100N/EPS"
@touch Graphs.tex
@echo \documentclass{article} >> Graphs.tex
@echo \usepackage[utf8]{inputenc} >> Graphs.tex
@echo \usepackage[landscape]{geometry} >> Graphs.tex
@echo \usepackage{graphicx} >> Graphs.tex
@echo \usepackage{epstopdf} >> Graphs.tex
@echo \title{Ring,10,50,5-25O-100N} >> Graphs.tex
@echo \begin{document} >> Graphs.tex
@echo \maketitle >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-50-5-25O-100N-messages-sankey.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-50-5-25O-100N-messages.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-50-5-25O-100N-cycleBreakdown.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-50-5-25O-100N-utilScatterAll-Fault.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-50-5-25O-100N-utility-final.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \end{document} >> Graphs.tex
pdflatex "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,10,50,5-25O-100N/EPS/Graphs.tex" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,10,50,5-25O-100N.txt" 2>&1
@sleep 1
@rm "Graphs.tex"
@rm "Graphs.aux"
@rm "Graphs.log"
@mv "Graphs.pdf" "C:/Users/Sam/Dropbox/PhD/ExperimentRes/_PDF/Ring-10-50-5-25O-100N.pdf"
@echo ""

@cd "C:/Users/Sam/git/PCD/PCD-PeerSim"
@echo Generating graphs for Ring,10,50,10-50O-50N...
@Rscript "C:/Users/Sam/Dropbox/PhD/ExperimentRes/graphs-new-debug.r" "Ring,10,50,10-50O-50N" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,10,50,10-50O-50N.txt" 2>&1
@cd "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,10,50,10-50O-50N/EPS"
@touch Graphs.tex
@echo \documentclass{article} >> Graphs.tex
@echo \usepackage[utf8]{inputenc} >> Graphs.tex
@echo \usepackage[landscape]{geometry} >> Graphs.tex
@echo \usepackage{graphicx} >> Graphs.tex
@echo \usepackage{epstopdf} >> Graphs.tex
@echo \title{Ring,10,50,10-50O-50N} >> Graphs.tex
@echo \begin{document} >> Graphs.tex
@echo \maketitle >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-50-10-50O-50N-messages-sankey.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-50-10-50O-50N-messages.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-50-10-50O-50N-cycleBreakdown.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-50-10-50O-50N-utilScatterAll-Fault.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-50-10-50O-50N-utility-final.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \end{document} >> Graphs.tex
pdflatex "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,10,50,10-50O-50N/EPS/Graphs.tex" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,10,50,10-50O-50N.txt" 2>&1
@sleep 1
@rm "Graphs.tex"
@rm "Graphs.aux"
@rm "Graphs.log"
@mv "Graphs.pdf" "C:/Users/Sam/Dropbox/PhD/ExperimentRes/_PDF/Ring-10-50-10-50O-50N.pdf"
@echo ""

@cd "C:/Users/Sam/git/PCD/PCD-PeerSim"
@echo Generating graphs for Ring,10,10,1-10O-100N...
@Rscript "C:/Users/Sam/Dropbox/PhD/ExperimentRes/graphs-new-debug.r" "Ring,10,10,1-10O-100N" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,10,10,1-10O-100N.txt" 2>&1
@cd "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,10,10,1-10O-100N/EPS"
@touch Graphs.tex
@echo \documentclass{article} >> Graphs.tex
@echo \usepackage[utf8]{inputenc} >> Graphs.tex
@echo \usepackage[landscape]{geometry} >> Graphs.tex
@echo \usepackage{graphicx} >> Graphs.tex
@echo \usepackage{epstopdf} >> Graphs.tex
@echo \title{Ring,10,10,1-10O-100N} >> Graphs.tex
@echo \begin{document} >> Graphs.tex
@echo \maketitle >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-10-1-10O-100N-messages-sankey.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-10-1-10O-100N-messages.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-10-1-10O-100N-cycleBreakdown.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-10-1-10O-100N-utilScatterAll-Fault.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-10-1-10O-100N-utility-final.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \end{document} >> Graphs.tex
pdflatex "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,10,10,1-10O-100N/EPS/Graphs.tex" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,10,10,1-10O-100N.txt" 2>&1
@sleep 1
@rm "Graphs.tex"
@rm "Graphs.aux"
@rm "Graphs.log"
@mv "Graphs.pdf" "C:/Users/Sam/Dropbox/PhD/ExperimentRes/_PDF/Ring-10-10-1-10O-100N.pdf"
@echo ""

@cd "C:/Users/Sam/git/PCD/PCD-PeerSim"
@echo Generating graphs for Ring,10,10,5-25O-100N...
@Rscript "C:/Users/Sam/Dropbox/PhD/ExperimentRes/graphs-new-debug.r" "Ring,10,10,5-25O-100N" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,10,10,5-25O-100N.txt" 2>&1
@cd "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,10,10,5-25O-100N/EPS"
@touch Graphs.tex
@echo \documentclass{article} >> Graphs.tex
@echo \usepackage[utf8]{inputenc} >> Graphs.tex
@echo \usepackage[landscape]{geometry} >> Graphs.tex
@echo \usepackage{graphicx} >> Graphs.tex
@echo \usepackage{epstopdf} >> Graphs.tex
@echo \title{Ring,10,10,5-25O-100N} >> Graphs.tex
@echo \begin{document} >> Graphs.tex
@echo \maketitle >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-10-5-25O-100N-messages-sankey.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-10-5-25O-100N-messages.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-10-5-25O-100N-cycleBreakdown.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-10-5-25O-100N-utilScatterAll-Fault.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-10-5-25O-100N-utility-final.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \end{document} >> Graphs.tex
pdflatex "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,10,10,5-25O-100N/EPS/Graphs.tex" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,10,10,5-25O-100N.txt" 2>&1
@sleep 1
@rm "Graphs.tex"
@rm "Graphs.aux"
@rm "Graphs.log"
@mv "Graphs.pdf" "C:/Users/Sam/Dropbox/PhD/ExperimentRes/_PDF/Ring-10-10-5-25O-100N.pdf"
@echo ""

@cd "C:/Users/Sam/git/PCD/PCD-PeerSim"
@echo Generating graphs for Ring,10,10,10-50O-50N...
@Rscript "C:/Users/Sam/Dropbox/PhD/ExperimentRes/graphs-new-debug.r" "Ring,10,10,10-50O-50N" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,10,10,10-50O-50N.txt" 2>&1
@cd "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,10,10,10-50O-50N/EPS"
@touch Graphs.tex
@echo \documentclass{article} >> Graphs.tex
@echo \usepackage[utf8]{inputenc} >> Graphs.tex
@echo \usepackage[landscape]{geometry} >> Graphs.tex
@echo \usepackage{graphicx} >> Graphs.tex
@echo \usepackage{epstopdf} >> Graphs.tex
@echo \title{Ring,10,10,10-50O-50N} >> Graphs.tex
@echo \begin{document} >> Graphs.tex
@echo \maketitle >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-10-10-50O-50N-messages-sankey.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-10-10-50O-50N-messages.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-10-10-50O-50N-cycleBreakdown.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-10-10-50O-50N-utilScatterAll-Fault.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-10-10-50O-50N-utility-final.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \end{document} >> Graphs.tex
pdflatex "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,10,10,10-50O-50N/EPS/Graphs.tex" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,10,10,10-50O-50N.txt" 2>&1
@sleep 1
@rm "Graphs.tex"
@rm "Graphs.aux"
@rm "Graphs.log"
@mv "Graphs.pdf" "C:/Users/Sam/Dropbox/PhD/ExperimentRes/_PDF/Ring-10-10-10-50O-50N.pdf"
@echo ""

@cd "C:/Users/Sam/git/PCD/PCD-PeerSim"
@echo Generating graphs for Ring,10,90,1-10O-100N...
@Rscript "C:/Users/Sam/Dropbox/PhD/ExperimentRes/graphs-new-debug.r" "Ring,10,90,1-10O-100N" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,10,90,1-10O-100N.txt" 2>&1
@cd "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,10,90,1-10O-100N/EPS"
@touch Graphs.tex
@echo \documentclass{article} >> Graphs.tex
@echo \usepackage[utf8]{inputenc} >> Graphs.tex
@echo \usepackage[landscape]{geometry} >> Graphs.tex
@echo \usepackage{graphicx} >> Graphs.tex
@echo \usepackage{epstopdf} >> Graphs.tex
@echo \title{Ring,10,90,1-10O-100N} >> Graphs.tex
@echo \begin{document} >> Graphs.tex
@echo \maketitle >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-90-1-10O-100N-messages-sankey.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-90-1-10O-100N-messages.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-90-1-10O-100N-cycleBreakdown.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-90-1-10O-100N-utilScatterAll-Fault.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-90-1-10O-100N-utility-final.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \end{document} >> Graphs.tex
pdflatex "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,10,90,1-10O-100N/EPS/Graphs.tex" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,10,90,1-10O-100N.txt" 2>&1
@sleep 1
@rm "Graphs.tex"
@rm "Graphs.aux"
@rm "Graphs.log"
@mv "Graphs.pdf" "C:/Users/Sam/Dropbox/PhD/ExperimentRes/_PDF/Ring-10-90-1-10O-100N.pdf"
@echo ""

@cd "C:/Users/Sam/git/PCD/PCD-PeerSim"
@echo Generating graphs for Ring,10,90,5-25O-100N...
@Rscript "C:/Users/Sam/Dropbox/PhD/ExperimentRes/graphs-new-debug.r" "Ring,10,90,5-25O-100N" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,10,90,5-25O-100N.txt" 2>&1
@cd "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,10,90,5-25O-100N/EPS"
@touch Graphs.tex
@echo \documentclass{article} >> Graphs.tex
@echo \usepackage[utf8]{inputenc} >> Graphs.tex
@echo \usepackage[landscape]{geometry} >> Graphs.tex
@echo \usepackage{graphicx} >> Graphs.tex
@echo \usepackage{epstopdf} >> Graphs.tex
@echo \title{Ring,10,90,5-25O-100N} >> Graphs.tex
@echo \begin{document} >> Graphs.tex
@echo \maketitle >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-90-5-25O-100N-messages-sankey.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-90-5-25O-100N-messages.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-90-5-25O-100N-cycleBreakdown.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-90-5-25O-100N-utilScatterAll-Fault.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-90-5-25O-100N-utility-final.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \end{document} >> Graphs.tex
pdflatex "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,10,90,5-25O-100N/EPS/Graphs.tex" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,10,90,5-25O-100N.txt" 2>&1
@sleep 1
@rm "Graphs.tex"
@rm "Graphs.aux"
@rm "Graphs.log"
@mv "Graphs.pdf" "C:/Users/Sam/Dropbox/PhD/ExperimentRes/_PDF/Ring-10-90-5-25O-100N.pdf"
@echo ""

@cd "C:/Users/Sam/git/PCD/PCD-PeerSim"
@echo Generating graphs for Ring,10,90,10-50O-50N...
@Rscript "C:/Users/Sam/Dropbox/PhD/ExperimentRes/graphs-new-debug.r" "Ring,10,90,10-50O-50N" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,10,90,10-50O-50N.txt" 2>&1
@cd "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,10,90,10-50O-50N/EPS"
@touch Graphs.tex
@echo \documentclass{article} >> Graphs.tex
@echo \usepackage[utf8]{inputenc} >> Graphs.tex
@echo \usepackage[landscape]{geometry} >> Graphs.tex
@echo \usepackage{graphicx} >> Graphs.tex
@echo \usepackage{epstopdf} >> Graphs.tex
@echo \title{Ring,10,90,10-50O-50N} >> Graphs.tex
@echo \begin{document} >> Graphs.tex
@echo \maketitle >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-90-10-50O-50N-messages-sankey.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-90-10-50O-50N-messages.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-90-10-50O-50N-cycleBreakdown.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-90-10-50O-50N-utilScatterAll-Fault.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-10-90-10-50O-50N-utility-final.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \end{document} >> Graphs.tex
pdflatex "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,10,90,10-50O-50N/EPS/Graphs.tex" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,10,90,10-50O-50N.txt" 2>&1
@sleep 1
@rm "Graphs.tex"
@rm "Graphs.aux"
@rm "Graphs.log"
@mv "Graphs.pdf" "C:/Users/Sam/Dropbox/PhD/ExperimentRes/_PDF/Ring-10-90-10-50O-50N.pdf"
@echo ""

@cd "C:/Users/Sam/git/PCD/PCD-PeerSim"
@echo Generating graphs for Ring,90,50,1-10O-100N...
@Rscript "C:/Users/Sam/Dropbox/PhD/ExperimentRes/graphs-new-debug.r" "Ring,90,50,1-10O-100N" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,90,50,1-10O-100N.txt" 2>&1
@cd "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,90,50,1-10O-100N/EPS"
@touch Graphs.tex
@echo \documentclass{article} >> Graphs.tex
@echo \usepackage[utf8]{inputenc} >> Graphs.tex
@echo \usepackage[landscape]{geometry} >> Graphs.tex
@echo \usepackage{graphicx} >> Graphs.tex
@echo \usepackage{epstopdf} >> Graphs.tex
@echo \title{Ring,90,50,1-10O-100N} >> Graphs.tex
@echo \begin{document} >> Graphs.tex
@echo \maketitle >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-50-1-10O-100N-messages-sankey.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-50-1-10O-100N-messages.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-50-1-10O-100N-cycleBreakdown.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-50-1-10O-100N-utilScatterAll-Fault.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-50-1-10O-100N-utility-final.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \end{document} >> Graphs.tex
pdflatex "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,90,50,1-10O-100N/EPS/Graphs.tex" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,90,50,1-10O-100N.txt" 2>&1
@sleep 1
@rm "Graphs.tex"
@rm "Graphs.aux"
@rm "Graphs.log"
@mv "Graphs.pdf" "C:/Users/Sam/Dropbox/PhD/ExperimentRes/_PDF/Ring-90-50-1-10O-100N.pdf"
@echo ""

@cd "C:/Users/Sam/git/PCD/PCD-PeerSim"
@echo Generating graphs for Ring,90,50,5-25O-100N...
@Rscript "C:/Users/Sam/Dropbox/PhD/ExperimentRes/graphs-new-debug.r" "Ring,90,50,5-25O-100N" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,90,50,5-25O-100N.txt" 2>&1
@cd "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,90,50,5-25O-100N/EPS"
@touch Graphs.tex
@echo \documentclass{article} >> Graphs.tex
@echo \usepackage[utf8]{inputenc} >> Graphs.tex
@echo \usepackage[landscape]{geometry} >> Graphs.tex
@echo \usepackage{graphicx} >> Graphs.tex
@echo \usepackage{epstopdf} >> Graphs.tex
@echo \title{Ring,90,50,5-25O-100N} >> Graphs.tex
@echo \begin{document} >> Graphs.tex
@echo \maketitle >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-50-5-25O-100N-messages-sankey.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-50-5-25O-100N-messages.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-50-5-25O-100N-cycleBreakdown.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-50-5-25O-100N-utilScatterAll-Fault.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-50-5-25O-100N-utility-final.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \end{document} >> Graphs.tex
pdflatex "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,90,50,5-25O-100N/EPS/Graphs.tex" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,90,50,5-25O-100N.txt" 2>&1
@sleep 1
@rm "Graphs.tex"
@rm "Graphs.aux"
@rm "Graphs.log"
@mv "Graphs.pdf" "C:/Users/Sam/Dropbox/PhD/ExperimentRes/_PDF/Ring-90-50-5-25O-100N.pdf"
@echo ""

@cd "C:/Users/Sam/git/PCD/PCD-PeerSim"
@echo Generating graphs for Ring,90,50,10-50O-50N...
@Rscript "C:/Users/Sam/Dropbox/PhD/ExperimentRes/graphs-new-debug.r" "Ring,90,50,10-50O-50N" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,90,50,10-50O-50N.txt" 2>&1
@cd "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,90,50,10-50O-50N/EPS"
@touch Graphs.tex
@echo \documentclass{article} >> Graphs.tex
@echo \usepackage[utf8]{inputenc} >> Graphs.tex
@echo \usepackage[landscape]{geometry} >> Graphs.tex
@echo \usepackage{graphicx} >> Graphs.tex
@echo \usepackage{epstopdf} >> Graphs.tex
@echo \title{Ring,90,50,10-50O-50N} >> Graphs.tex
@echo \begin{document} >> Graphs.tex
@echo \maketitle >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-50-10-50O-50N-messages-sankey.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-50-10-50O-50N-messages.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-50-10-50O-50N-cycleBreakdown.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-50-10-50O-50N-utilScatterAll-Fault.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-50-10-50O-50N-utility-final.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \end{document} >> Graphs.tex
pdflatex "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,90,50,10-50O-50N/EPS/Graphs.tex" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,90,50,10-50O-50N.txt" 2>&1
@sleep 1
@rm "Graphs.tex"
@rm "Graphs.aux"
@rm "Graphs.log"
@mv "Graphs.pdf" "C:/Users/Sam/Dropbox/PhD/ExperimentRes/_PDF/Ring-90-50-10-50O-50N.pdf"
@echo ""

@cd "C:/Users/Sam/git/PCD/PCD-PeerSim"
@echo Generating graphs for Ring,90,10,1-10O-100N...
@Rscript "C:/Users/Sam/Dropbox/PhD/ExperimentRes/graphs-new-debug.r" "Ring,90,10,1-10O-100N" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,90,10,1-10O-100N.txt" 2>&1
@cd "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,90,10,1-10O-100N/EPS"
@touch Graphs.tex
@echo \documentclass{article} >> Graphs.tex
@echo \usepackage[utf8]{inputenc} >> Graphs.tex
@echo \usepackage[landscape]{geometry} >> Graphs.tex
@echo \usepackage{graphicx} >> Graphs.tex
@echo \usepackage{epstopdf} >> Graphs.tex
@echo \title{Ring,90,10,1-10O-100N} >> Graphs.tex
@echo \begin{document} >> Graphs.tex
@echo \maketitle >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-10-1-10O-100N-messages-sankey.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-10-1-10O-100N-messages.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-10-1-10O-100N-cycleBreakdown.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-10-1-10O-100N-utilScatterAll-Fault.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-10-1-10O-100N-utility-final.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \end{document} >> Graphs.tex
pdflatex "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,90,10,1-10O-100N/EPS/Graphs.tex" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,90,10,1-10O-100N.txt" 2>&1
@sleep 1
@rm "Graphs.tex"
@rm "Graphs.aux"
@rm "Graphs.log"
@mv "Graphs.pdf" "C:/Users/Sam/Dropbox/PhD/ExperimentRes/_PDF/Ring-90-10-1-10O-100N.pdf"
@echo ""

@cd "C:/Users/Sam/git/PCD/PCD-PeerSim"
@echo Generating graphs for Ring,90,10,5-25O-100N...
@Rscript "C:/Users/Sam/Dropbox/PhD/ExperimentRes/graphs-new-debug.r" "Ring,90,10,5-25O-100N" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,90,10,5-25O-100N.txt" 2>&1
@cd "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,90,10,5-25O-100N/EPS"
@touch Graphs.tex
@echo \documentclass{article} >> Graphs.tex
@echo \usepackage[utf8]{inputenc} >> Graphs.tex
@echo \usepackage[landscape]{geometry} >> Graphs.tex
@echo \usepackage{graphicx} >> Graphs.tex
@echo \usepackage{epstopdf} >> Graphs.tex
@echo \title{Ring,90,10,5-25O-100N} >> Graphs.tex
@echo \begin{document} >> Graphs.tex
@echo \maketitle >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-10-5-25O-100N-messages-sankey.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-10-5-25O-100N-messages.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-10-5-25O-100N-cycleBreakdown.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-10-5-25O-100N-utilScatterAll-Fault.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-10-5-25O-100N-utility-final.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \end{document} >> Graphs.tex
pdflatex "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,90,10,5-25O-100N/EPS/Graphs.tex" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,90,10,5-25O-100N.txt" 2>&1
@sleep 1
@rm "Graphs.tex"
@rm "Graphs.aux"
@rm "Graphs.log"
@mv "Graphs.pdf" "C:/Users/Sam/Dropbox/PhD/ExperimentRes/_PDF/Ring-90-10-5-25O-100N.pdf"
@echo ""

@cd "C:/Users/Sam/git/PCD/PCD-PeerSim"
@echo Generating graphs for Ring,90,10,10-50O-50N...
@Rscript "C:/Users/Sam/Dropbox/PhD/ExperimentRes/graphs-new-debug.r" "Ring,90,10,10-50O-50N" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,90,10,10-50O-50N.txt" 2>&1
@cd "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,90,10,10-50O-50N/EPS"
@touch Graphs.tex
@echo \documentclass{article} >> Graphs.tex
@echo \usepackage[utf8]{inputenc} >> Graphs.tex
@echo \usepackage[landscape]{geometry} >> Graphs.tex
@echo \usepackage{graphicx} >> Graphs.tex
@echo \usepackage{epstopdf} >> Graphs.tex
@echo \title{Ring,90,10,10-50O-50N} >> Graphs.tex
@echo \begin{document} >> Graphs.tex
@echo \maketitle >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-10-10-50O-50N-messages-sankey.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-10-10-50O-50N-messages.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-10-10-50O-50N-cycleBreakdown.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-10-10-50O-50N-utilScatterAll-Fault.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-10-10-50O-50N-utility-final.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \end{document} >> Graphs.tex
pdflatex "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,90,10,10-50O-50N/EPS/Graphs.tex" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,90,10,10-50O-50N.txt" 2>&1
@sleep 1
@rm "Graphs.tex"
@rm "Graphs.aux"
@rm "Graphs.log"
@mv "Graphs.pdf" "C:/Users/Sam/Dropbox/PhD/ExperimentRes/_PDF/Ring-90-10-10-50O-50N.pdf"
@echo ""

@cd "C:/Users/Sam/git/PCD/PCD-PeerSim"
@echo Generating graphs for Ring,90,90,1-10O-100N...
@Rscript "C:/Users/Sam/Dropbox/PhD/ExperimentRes/graphs-new-debug.r" "Ring,90,90,1-10O-100N" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,90,90,1-10O-100N.txt" 2>&1
@cd "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,90,90,1-10O-100N/EPS"
@touch Graphs.tex
@echo \documentclass{article} >> Graphs.tex
@echo \usepackage[utf8]{inputenc} >> Graphs.tex
@echo \usepackage[landscape]{geometry} >> Graphs.tex
@echo \usepackage{graphicx} >> Graphs.tex
@echo \usepackage{epstopdf} >> Graphs.tex
@echo \title{Ring,90,90,1-10O-100N} >> Graphs.tex
@echo \begin{document} >> Graphs.tex
@echo \maketitle >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-90-1-10O-100N-messages-sankey.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-90-1-10O-100N-messages.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-90-1-10O-100N-cycleBreakdown.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-90-1-10O-100N-utilScatterAll-Fault.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-90-1-10O-100N-utility-final.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \end{document} >> Graphs.tex
pdflatex "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,90,90,1-10O-100N/EPS/Graphs.tex" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,90,90,1-10O-100N.txt" 2>&1
@sleep 1
@rm "Graphs.tex"
@rm "Graphs.aux"
@rm "Graphs.log"
@mv "Graphs.pdf" "C:/Users/Sam/Dropbox/PhD/ExperimentRes/_PDF/Ring-90-90-1-10O-100N.pdf"
@echo ""

@cd "C:/Users/Sam/git/PCD/PCD-PeerSim"
@echo Generating graphs for Ring,90,90,5-25O-100N...
@Rscript "C:/Users/Sam/Dropbox/PhD/ExperimentRes/graphs-new-debug.r" "Ring,90,90,5-25O-100N" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,90,90,5-25O-100N.txt" 2>&1
@cd "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,90,90,5-25O-100N/EPS"
@touch Graphs.tex
@echo \documentclass{article} >> Graphs.tex
@echo \usepackage[utf8]{inputenc} >> Graphs.tex
@echo \usepackage[landscape]{geometry} >> Graphs.tex
@echo \usepackage{graphicx} >> Graphs.tex
@echo \usepackage{epstopdf} >> Graphs.tex
@echo \title{Ring,90,90,5-25O-100N} >> Graphs.tex
@echo \begin{document} >> Graphs.tex
@echo \maketitle >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-90-5-25O-100N-messages-sankey.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-90-5-25O-100N-messages.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-90-5-25O-100N-cycleBreakdown.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-90-5-25O-100N-utilScatterAll-Fault.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-90-5-25O-100N-utility-final.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \end{document} >> Graphs.tex
pdflatex "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,90,90,5-25O-100N/EPS/Graphs.tex" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,90,90,5-25O-100N.txt" 2>&1
@sleep 1
@rm "Graphs.tex"
@rm "Graphs.aux"
@rm "Graphs.log"
@mv "Graphs.pdf" "C:/Users/Sam/Dropbox/PhD/ExperimentRes/_PDF/Ring-90-90-5-25O-100N.pdf"
@echo ""

@cd "C:/Users/Sam/git/PCD/PCD-PeerSim"
@echo Generating graphs for Ring,90,90,10-50O-50N...
@Rscript "C:/Users/Sam/Dropbox/PhD/ExperimentRes/graphs-new-debug.r" "Ring,90,90,10-50O-50N" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,90,90,10-50O-50N.txt" 2>&1
@cd "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,90,90,10-50O-50N/EPS"
@touch Graphs.tex
@echo \documentclass{article} >> Graphs.tex
@echo \usepackage[utf8]{inputenc} >> Graphs.tex
@echo \usepackage[landscape]{geometry} >> Graphs.tex
@echo \usepackage{graphicx} >> Graphs.tex
@echo \usepackage{epstopdf} >> Graphs.tex
@echo \title{Ring,90,90,10-50O-50N} >> Graphs.tex
@echo \begin{document} >> Graphs.tex
@echo \maketitle >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-90-10-50O-50N-messages-sankey.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-90-10-50O-50N-messages.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-90-10-50O-50N-cycleBreakdown.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-90-10-50O-50N-utilScatterAll-Fault.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \includegraphics[width=\textwidth]{Ring-90-90-10-50O-50N-utility-final.eps} >> Graphs.tex
@echo \newpage >> Graphs.tex
@echo \end{document} >> Graphs.tex
pdflatex "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/Ring,90,90,10-50O-50N/EPS/Graphs.tex" >> "C:/Users/Sam/Documents/Dropbox Overflow/PhD/ExperimentRes/csv/_Logs/Ring,90,90,10-50O-50N.txt" 2>&1
@sleep 1
@rm "Graphs.tex"
@rm "Graphs.aux"
@rm "Graphs.log"
@mv "Graphs.pdf" "C:/Users/Sam/Dropbox/PhD/ExperimentRes/_PDF/Ring-90-90-10-50O-50N.pdf"
@echo ""

