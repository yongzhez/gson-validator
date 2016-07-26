# gson-validator
gson-validator is a library to verify json against json-schema.
The goal is to have a working complete validation for json-schema's most 
recent version (currently at the time of writing, v4) that can be merged
into GSON library since it currently is missing this functionality. 

###*Gson Goals*
  * Provide simple json schema to json validation from either json object, String or file
  * Integrate into GSON library so users only need one library
  * Provide clear and percise error logging for why and where json failed to validate
  
