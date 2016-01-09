CASINO = ./POOCasino.class
PLAYER = ./lib/PlayerB02902009.class

run: $(CASINO) $(PLAYER)
	java -cp .:./lib POOCasino 0 0 PlayerB02902009 PlayerB02902009 PlayerB02902009 PlayerB02902009

$(PLAYER): ./src/PlayerB02902009.java
	javac ./src/PlayerB02902009.java -d ./lib -cp ./lib

$(CASINO): ./src/POOCasino.java
	javac ./src/POOCasino.java -d ./ -cp ./lib

clean:
	rm ./*.class ./lib/PlayerB02902009.class
