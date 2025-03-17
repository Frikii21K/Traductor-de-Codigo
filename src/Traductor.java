import java.util.ArrayList;
import java.util.List;

public class Traductor {

    // Clase para almacenar información de errores en la traducción
    public static class ErrorInfo {
        public int linea;
        public String mensaje;

        public ErrorInfo(int linea, String mensaje) {
            this.linea = linea;
            this.mensaje = mensaje;
        }
    }

    // Clase que representa el resultado de la traducción
    public static class ResultadoTraduccion {
        public String traduccion;
        public List<ErrorInfo> errores;

        public ResultadoTraduccion(String traduccion, List<ErrorInfo> errores) {
            this.traduccion = traduccion;
            this.errores = errores;
        }
    }

    /**
     * Método principal que realiza la traducción del código.
     * Primero valida que el código corresponda al lenguaje de origen y luego traduce línea a línea.
     *
     * @param codigo       Código fuente a traducir.
     * @param idiomaOrigen Idioma de origen (por ejemplo, "Java", "C++", "JS").
     * @param idiomaDestino Idioma destino (por ejemplo, "JS" o "C++").
     * @return Objeto ResultadoTraduccion con el código traducido y la lista de errores.
     * @throws Exception Si el código es nulo o está vacío.
     */
    public static ResultadoTraduccion traducir(String codigo, String idiomaOrigen, String idiomaDestino) throws Exception {
        List<ErrorInfo> errores = new ArrayList<>();

        if (codigo == null || codigo.trim().isEmpty()) {
            throw new Exception("El código es nulo o está vacío.");
        }

        // Validar que el código fuente tenga los elementos mínimos del idioma de origen.
        if (!validarCodigo(codigo, idiomaOrigen, errores)) {
            return new ResultadoTraduccion("", errores);
        }

        StringBuilder traduccion = new StringBuilder();
        String[] lineas = codigo.split("\\n");
        int numLinea = 1;
        for (String linea : lineas) {
            if (linea.contains("error")) {
                errores.add(new ErrorInfo(numLinea, "Se encontró la palabra 'error' en la línea."));
                numLinea++;
                continue;
            }

            String lineaTraducida = traducirLinea(linea, idiomaOrigen, idiomaDestino, numLinea, errores);
            traduccion.append(lineaTraducida).append("\n");
            numLinea++;
        }

        return new ResultadoTraduccion(traduccion.toString(), errores);
    }

    /*Valida que el código fuente corresponda al idioma de origen esperado.*/
    private static boolean validarCodigo(String codigo, String idiomaOrigen, List<ErrorInfo> errores) {
        boolean valido = true;
        if (idiomaOrigen.equals("Java")) {
            if (!codigo.contains("class") || !codigo.contains("main(")) {
                errores.add(new ErrorInfo(1, "El código fuente no parece ser Java (faltan 'class' o 'main')."));
                valido = false;
            }
        } else if (idiomaOrigen.equals("C++")) {
            if (!codigo.contains("#include") || !codigo.contains("main(")) {
                errores.add(new ErrorInfo(1, "El código fuente no parece ser C++ (faltan '#include' o 'main')."));
                valido = false;
            }
        } else if (idiomaOrigen.equals("JS")) {
            if (!codigo.contains("function") && !codigo.contains("console.log")) {
                errores.add(new ErrorInfo(1, "El código fuente no parece ser JavaScript (faltan 'function' o 'console.log')."));
                valido = false;
            }
        }
        return valido;
    }


    private static String traducirLinea(String linea, String idiomaOrigen, String idiomaDestino, int numLinea, List<ErrorInfo> errores) {
        // Si el idioma de origen y destino son iguales, se retorna la línea sin cambios.
        if (idiomaOrigen.equals(idiomaDestino)) {
            return linea;
        }

        if (idiomaOrigen.equals("Java")) {
            if (idiomaDestino.equals("JS")) {
                return traducirLineaJavaToJS(linea, numLinea, errores);
            } else if (idiomaDestino.equals("C++")) {
                return traducirLineaJavaToCpp(linea, numLinea, errores);
            }
        } else if (idiomaOrigen.equals("C++")) {
            if (idiomaDestino.equals("JS")) {
                return traducirLineaCppToJS(linea, numLinea, errores);
            } else if (idiomaDestino.equals("Java")) {
                return traducirLineaCppToJava(linea, numLinea, errores);
            }
        } else if (idiomaOrigen.equals("JS")) {
            if (idiomaDestino.equals("Java")) {
                return traducirLineaJSToJava(linea, numLinea, errores);
            } else if (idiomaDestino.equals("C++")) {
                return traducirLineaJSToCpp(linea, numLinea, errores);
            }
        }
        // Si la conversión no está soportada, se notifica el error.
        errores.add(new ErrorInfo(numLinea, "Conversión de " + idiomaOrigen + " a " + idiomaDestino + " no soportada."));
        return linea;
    }

    /*Traduce una línea de código de Java a JavaScript.*/
    private static String traducirLineaJavaToJS(String linea, int numLinea, List<ErrorInfo> errores) {
        String resultado = linea;
        if (resultado.trim().startsWith("public class")) {
            return "// Clase omitida en JS";
        }
        if (resultado.contains("public static void main")) {
            resultado = resultado.replace("public static void main(String[] args)", "function main()");
        }
        if (resultado.contains("System.out.println")) {
            resultado = resultado.replace("System.out.println", "console.log");
        }
        return resultado;
    }

    /*Traduce una línea de código de Java a C++.*/
    private static String traducirLineaJavaToCpp(String linea, int numLinea, List<ErrorInfo> errores) {
        String resultado = linea;
        if (resultado.trim().startsWith("public class")) {
            return "// Clase traducida omitida en C++";
        }
        if (resultado.contains("public static void main")) {
            resultado = resultado.replace("public static void main(String[] args)", "int main()");
        }
        if (resultado.contains("System.out.println")) {
            resultado = resultado.replace("System.out.println", "std::cout << ");
            if (resultado.contains(");")) {
                resultado = resultado.replace(");", " << std::endl;");
            }
        }
        return resultado;
    }

    /*Traduce una línea de código de C++ a JavaScript.*/
    private static String traducirLineaCppToJS(String linea, int numLinea, List<ErrorInfo> errores) {
        String resultado = linea;

        if (resultado.trim().startsWith("#include")) {
            return "// Directiva de preprocesador omitida";
        }
        if (resultado.trim().startsWith("using namespace")) {
            return "";
        }
        if (resultado.contains("int main(")) {
            resultado = resultado.replace("int main()", "function main()");
        }
        if (resultado.contains("std::cout")) {
            int start = resultado.indexOf("std::cout");
            int posInicio = resultado.indexOf("<<", start);
            int posFin = resultado.indexOf("<< std::endl");
            if (posInicio != -1 && posFin != -1 && posFin > posInicio) {
                String contenido = resultado.substring(posInicio + 2, posFin).trim();
                resultado = "console.log(" + contenido + ");";
            } else {
                resultado = resultado.replace("std::cout", "console.log");
            }
        }
        return resultado;
    }

    /*Traduce una línea de código de C++ a Java.*/
    private static String traducirLineaCppToJava(String linea, int numLinea, List<ErrorInfo> errores) {
        String resultado = linea;
        if (resultado.contains("int main(")) {
            resultado = resultado.replace("int main()", "public static void main(String[] args)");
        }
        if (resultado.contains("console.log")) {
            resultado = resultado.replace("console.log", "System.out.println");
        }
        if (resultado.trim().startsWith("#include") || resultado.trim().startsWith("using namespace")) {
            return "// Directiva omitida en Java";
        }
        return resultado;
    }

    /*Traduce una línea de código de JavaScript a Java.*/
    private static String traducirLineaJSToJava(String linea, int numLinea, List<ErrorInfo> errores) {
        String resultado = linea;
        if (resultado.contains("console.log")) {
            resultado = resultado.replace("console.log", "System.out.println");
        }
        return resultado;
    }

    /* Traduce una línea de código de JavaScript a C++.*/
    private static String traducirLineaJSToCpp(String linea, int numLinea, List<ErrorInfo> errores) {
        String resultado = linea;
        if (resultado.contains("console.log")) {
            resultado = resultado.replace("console.log", "std::cout << ");
            if (resultado.contains(");")) {
                resultado = resultado.replace(");", " << std::endl;");
            }
        }
        return resultado;
    }
}
