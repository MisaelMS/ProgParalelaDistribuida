package BuscaPorThreads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Result {
    private Boolean found = false;
    private String directory;
    private String file;
    private Integer line;
    private String error;

    public Result throwError(){
        this.error = "Arquivo não encontrado";
        return this;
    }
}
