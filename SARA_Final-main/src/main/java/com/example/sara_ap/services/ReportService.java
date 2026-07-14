package com.example.sara_ap.services;

import com.example.sara_ap.controller.TeacherController.FinalRow;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.*;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.List;

public class ReportService {

    public static boolean exportToPDF(File file, String nombreCurso, List<FinalRow> filasFinales) {
        // Configuramos la página A4 en orientación vertical con márgenes proporcionados de 36 puntos
        Document document = new Document(PageSize.A4, 36, 36, 54, 54);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            // 🎨 DEFINICIÓN DE TIPOGRAFÍAS Y PALETA DE COLORES PREMIUM (Gama Politécnica)
            java.awt.Color colorPrimario = new java.awt.Color(15, 23, 42);     // Azul Medianoche Oscuro
            java.awt.Color colorSecundario = new java.awt.Color(2, 132, 199);  // Azul Institucional
            java.awt.Color colorTexto = new java.awt.Color(51, 65, 85);        // Gris Pizarra
            java.awt.Color colorBorde = new java.awt.Color(226, 232, 240);    // Gris Claro para bordes finos

            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Font.BOLD, colorPrimario);
            Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL, colorTexto);
            Font fontMeta = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Font.BOLD, colorSecundario);
            Font fontCabecera = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Font.BOLD, java.awt.Color.WHITE);
            Font fontCelda = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL, colorTexto);
            Font fontCeldaBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Font.BOLD, colorPrimario);

            // 🏢 ENCABEZADO ESTILIZADO
            Paragraph titulo = new Paragraph("SISTEMA DE APOYO AL RENDIMIENTO ACADÉMICO", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            Paragraph appName = new Paragraph("SARA — REPORTE DE CALIFICACIONES SEMESTRALES", fontSubtitulo);
            appName.setAlignment(Element.ALIGN_CENTER);
            appName.setSpacingBefore(2);
            document.add(appName);

            // Línea divisoria sutil
            Paragraph lineaDivisoria = new Paragraph("_________________________________________________________________________________", fontSubtitulo);
            lineaDivisoria.setAlignment(Element.ALIGN_CENTER);
            lineaDivisoria.setSpacingAfter(15);
            document.add(lineaDivisoria);

            // 📋 METADATOS DEL CURSO
            Paragraph metaInfo = new Paragraph();
            metaInfo.setFont(fontSubtitulo);
            metaInfo.add(new Phrase("Asignatura: ", fontMeta));
            metaInfo.add(nombreCurso + "     |     ");
            metaInfo.add(new Phrase("Fecha de Emisión: ", fontMeta));
            metaInfo.add(LocalDate.now().toString() + "\n\n");
            metaInfo.setAlignment(Element.ALIGN_LEFT);
            document.add(metaInfo);

            // 📊 CONFIGURACIÓN ESTRICTA DE LA TABLA (6 Columnas exactas)
            PdfPTable tabla = new PdfPTable(6);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{15f, 33f, 13f, 13f, 13f, 13f}); // Suma exacta del 100%
            tabla.setSpacingBefore(5);
            tabla.setSpacingAfter(40);

            // Cabeceras de la Tabla (6 columnas)
            String[] cabeceras = {"Cédula", "Estudiante", "Nota B1", "Nota B2", "Promedio", "Estado"};
            for (String c : cabeceras) {
                PdfPCell cell = new PdfPCell(new Phrase(c, fontCabecera));
                cell.setBackgroundColor(colorSecundario);
                cell.setBorderColor(colorSecundario);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(8);
                tabla.addCell(cell);
            }

            // 🔄 VOLCADO DE REGISTROS EXACTO (Alineación perfecta uno a uno)
            boolean filaPar = false;
            for (FinalRow row : filasFinales) {
                java.awt.Color colorFondoFila = filaPar ? new java.awt.Color(248, 250, 252) : java.awt.Color.WHITE;

                // 1. Cédula
                tabla.addCell(crearCelda(row.idProperty().get(), fontCelda, colorFondoFila, colorBorde, Element.ALIGN_CENTER));

                // 2. Estudiante
                tabla.addCell(crearCelda("  " + row.nombreProperty().get(), fontCelda, colorFondoFila, colorBorde, Element.ALIGN_LEFT));

                // 3. Nota B1
                tabla.addCell(crearCelda(row.b1Property().get(), fontCelda, colorFondoFila, colorBorde, Element.ALIGN_CENTER));

                // 4. Nota B2
                tabla.addCell(crearCelda(row.b2Property().get(), fontCelda, colorFondoFila, colorBorde, Element.ALIGN_CENTER));

                // 5. Promedio Semestral
                tabla.addCell(crearCelda(row.notaFinalProperty().get(), fontCeldaBold, colorFondoFila, colorBorde, Element.ALIGN_CENTER));

                // 6. Estado Definitivo con Semáforo Estilizado
                String notaTexto = row.notaFinalProperty().get().replace(",", ".");
                double notaFinal = Double.parseDouble(notaTexto);

                String textoEstado = "REPROBADO";
                java.awt.Color colorEstado = new java.awt.Color(254, 226, 226);     // Rojo Pastel Suave
                java.awt.Color colorTextoEstado = new java.awt.Color(185, 28, 28);  // Texto Rojo Oscuro

                if (notaFinal >= 14.0) {
                    textoEstado = "APROBADO";
                    colorEstado = new java.awt.Color(220, 252, 231);     // Verde Pastel Suave
                    colorTextoEstado = new java.awt.Color(21, 128, 61);  // Texto Verde Oscuro
                } else if (notaFinal >= 12.0) {
                    textoEstado = "A SUPLETORIO";
                    colorEstado = new java.awt.Color(254, 243, 199);     // Amarillo Pastel Suave
                    colorTextoEstado = new java.awt.Color(180, 83, 9);   // Texto Amarillo Oscuro
                }

                Font fontEstado = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Font.BOLD, colorTextoEstado);
                PdfPCell cellEstado = new PdfPCell(new Phrase(textoEstado, fontEstado));
                cellEstado.setBackgroundColor(colorEstado);
                cellEstado.setBorderColor(colorBorde);
                cellEstado.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellEstado.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cellEstado.setPadding(6);
                tabla.addCell(cellEstado); // Cerramos la sexta columna exacta

                filaPar = !filaPar;
            }

            document.add(tabla);

            // ✍️ SECCIÓN DE FIRMAS DE RESPONSABILIDAD
            Paragraph espacioFirmas = new Paragraph("\n\n\n_____________________________________\n", fontCeldaBold);
            espacioFirmas.add(new Phrase("Firma del Docente Responsable", fontSubtitulo));
            espacioFirmas.setAlignment(Element.ALIGN_CENTER);
            document.add(espacioFirmas);

            return true;

        } catch (Exception e) {
            System.err.println("❌ Error de formateo en ReportService: " + e.getMessage());
            return false;
        } finally {
            if (document.isOpen()) document.close();
        }
    }

    private static PdfPCell crearCelda(String texto, Font fuente, java.awt.Color fondo, java.awt.Color colorBorde, int alineacion) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, fuente));
        cell.setBackgroundColor(fondo);
        cell.setBorderColor(colorBorde);
        cell.setHorizontalAlignment(alineacion);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(7);
        return cell;
    }
}