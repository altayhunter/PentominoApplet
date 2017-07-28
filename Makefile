TARGET = Main
SRC = $(wildcard *.java)
OBJ = $(patsubst %.java, %.class, $(SRC))
JFLAGS = -Xlint:all

.PHONY: run lint clean

$(OBJ): %.class : %.java
	javac $< $(JFLAGS)

run: $(OBJ)
	java $(TARGET)

lint:
	java -jar /opt/findbugs/lib/findbugs.jar .

clean:
	-rm *.class
