java -classpath .;lib/peersim-1.0.5.jar;lib/jpl.jar;lib/jep-2.3.0.jar;lib/djep-1.0.0.jar;lin/peersim-doclet.jar;bin peersim.Simulator "PCD-config.txt"
"C:/Users/Sam/Tools/R/R-3.3.1/bin/R.exe" CMD BATCH  --no-save --no-restore "C:/Users/Sam/Dropbox/PhD/PCD-PeerSim/res/graphs.r" "C:/Users/Sam/Dropbox/PhD/PCD-PeerSim/res/graphs-log.txt"
pause