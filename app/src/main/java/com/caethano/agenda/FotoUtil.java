package com.caethano.agenda;

import android.net.Uri;
import android.widget.ImageView;

/** Utilitário para exibir a foto do contato a partir da URI salva no banco. */
public final class FotoUtil {

    private FotoUtil() { }

    public static void exibir(ImageView imageView, String foto) {
        if (foto == null || foto.isEmpty()) {
            imageView.setImageResource(R.drawable.ic_pessoa);
            return;
        }
        try {
            imageView.setImageURI(Uri.parse(foto));
            if (imageView.getDrawable() == null) {
                imageView.setImageResource(R.drawable.ic_pessoa);
            }
        } catch (Exception e) {
            // a imagem pode ter sido apagada da galeria ou a permissão revogada
            imageView.setImageResource(R.drawable.ic_pessoa);
        }
    }
}
