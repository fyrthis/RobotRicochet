#include <stdio.h>       /* standard I/O routines                     */
#include <stdlib.h>      /* rand() and srand() functions              */

#include <string.h>

#define CHUNK 1024

int size_x = 0, size_y = 0;
int x = 0, y = 0;
char *buff = "";

int main(int argc, char* argv[]) {
	
    // le fichier est passé en parametre
    // si pas de parametre, alors le fichier est BasicGrid.txt
    char *fileName = NULL;
	if(argv[1] == NULL){
	  	fileName = "../res/BasicGrid.txt";
	  	fprintf(stderr, "%s\n", fileName);
	}
    FILE* file = fopen(fileName, "r"); /* should check the result */
    
    if(file == NULL){
    	fprintf(stderr, "Error: Could not open file\n");
    	exit(-1);
    }

    int infoSize = 0;
    char line[128];

    // Ignore comments line
    while (fgets(line, sizeof(line), file)) {
        if(strncmp(line, "##", 2) != 0)
            break;
    }

    // Get the line size info
    char * pch = strtok(line, " ");
    while(pch != NULL){
        size_x = atoi(pch);
        printf("size_x: %s\n", pch);
        pch = strtok(NULL, " \n");
        size_y = atoi(pch);
        printf("size_y: %s\n", pch);
        pch = strtok(NULL, " \n");
    }

    int grid[size_x][size_y];
    int x = 0;
    int y = 0;

    // Get the grid informations
    while (fgets(line, sizeof(line), file)) {
       if(strncmp(line, "END", 3) == 0)
            break;

        char * pch = strtok(line, " ");

        while(pch != NULL){
            grid[x][y] = atoi(pch);
            pch = strtok(NULL, " ");
            y++;
            if(y == size_x){
                y = 0;
                x++;
            }
        }
    }

    int i = 0, j = 0;

    for(i = 0; i < size_x; i++){
        for(j = 0; j < size_y; j++){
            printf("%d ", grid[i][j]);
        }
        printf("\n");
    }

    /* may check feof here to make a difference between eof and io failure -- network
       timeout for instance */

    fclose(file);

    return 0;

}