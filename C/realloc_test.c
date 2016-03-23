#include <stdio.h>
#include <stdlib.h>
#include <string.h>

char * append_strings(const char * old, const char * new);

char * append_strings(const char * old, const char * new)
{
    // find the size of the string to allocate
    const size_t old_len = strlen(old), new_len = strlen(new);
    const size_t out_len = old_len + new_len + 1;

    // allocate a pointer to the new string
    char *out = malloc(out_len);

    // concat both strings and return
    memcpy(out, old, old_len);
    memcpy(out + old_len, new, new_len + 1);

    return out;
}

int main(int argc, char* argv[])
{

   char *c1 = malloc(5);
   c1 = "Hello";
   char *c2 = malloc(5);
   c2 = "World";
   char* c = append_strings(c1, c2);
   fprintf(stderr, "\tt : \n");
   fprintf(stderr, "%s\n", c1);
   fprintf(stderr, "%s\n", c2);
   fprintf(stderr, "%s\n", c);

   return 0;
}