import * as React from 'react';
import * as monaco from 'monaco-editor-core';
import RdevsLangService from '../../language-service/LanguageService';

const BASE_URL = 'http://a02-devs-backend:8092/b02';

interface IEditorProps {
  language: string;
}

interface ICustomEditor extends monaco.editor.IStandaloneCodeEditor {}


const Editor: React.FC<IEditorProps> = (props: IEditorProps) => {
  const editorRef = React.useRef<ICustomEditor | null>(null);
  const [errorDialogOpen, setErrorDialogOpen] = React.useState<boolean>(false);
  const [errorDialogMessage, setErrorDialogMessage] = React.useState<string>('');
  const [successDialogOpen, setSuccessDialogOpen] = React.useState<boolean>(false);
  const [successMessage, setSuccessMessage] = React.useState<string>('');
  const [imageBase64, setImageBase64] = React.useState<string>();

  React.useEffect(() => {

    const imgElement = document.getElementById('imagen-scrollable') as HTMLImageElement | null;
    if (imgElement) {
      imgElement.src = '../public/imagen_ejemplo.jpeg'; 
    }

    if (editorRef.current) {
      editorRef.current.dispose();
    }
    
    editorRef.current = monaco.editor.create(document.getElementById('editor')!, {
      value: `//This is an example
//Network definition
The routingProcess network includes Machine1, Machine2 and Machine3
//Behaviors definition
The Machine1 materializes MACHINETYPEA
The behaviors are defined as: (MACHINETYPEB,Machine2), (MACHINETYPEC,Machine3)
//Interactions definition
The Machine1 sends outputs to Machine2
the output of Machine2 is connected to the inputs of Machine3
The Machine3 receives inputs from Machine1`,
      language: props.language,
      automaticLayout: true,
      readOnly: false,
      cursorStyle: 'line',
      cursorBlinking: 'smooth',
      wordWrap: 'on',
      minimap: {
        enabled: false,
      },
      scrollbar: {
        vertical: 'auto',
        horizontal: 'auto',
      },
      lineNumbers: 'on',
      glyphMargin: true,
      renderLineHighlight: 'all',
      renderIndentGuides: true,
      renderWhitespace: 'none',
    });

    const obtenerContenidoBtn = document.getElementById('obtener-contenido');
    if (obtenerContenidoBtn) {
      obtenerContenidoBtn.addEventListener('click', handleContentButtonClick);
    }

    const obtenerImagenBtn = document.getElementById('obtener-imagen');
    if (obtenerImagenBtn) {
      obtenerImagenBtn.addEventListener('click', handleImageButtonClick);
    }

    return () => {
      if (editorRef.current) {
        editorRef.current.dispose();
      }
      if (obtenerContenidoBtn) {
        obtenerContenidoBtn.removeEventListener('click', handleContentButtonClick);
      }
      if (obtenerImagenBtn) {
        obtenerImagenBtn.removeEventListener('click', handleImageButtonClick);
      }
    };
  }, [props.language]);

  const handleContentButtonClick = async () => {
    if (editorRef.current) {
      const contenidoEditor = editorRef.current.getValue();
      const service = new RdevsLangService();
      const syntaxErrors = service.validate(contenidoEditor);

      if (syntaxErrors.length > 0) {
        setErrorDialogMessage('The content contains syntax errors. Please correct them before obtaining the implementation.');
        setErrorDialogOpen(true);
        return;
      }

      try {
        const response = await fetch(`${BASE_URL}/mensaje/post`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ contenido: contenidoEditor }),
        });

        if (response.ok) {
                    
          const blob = await response.blob();
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.style.display = 'none';
          a.href = url;
          a.download = 'rdevsImplementationProject.zip';
          document.body.appendChild(a);
          a.click();
          window.URL.revokeObjectURL(url);

          setSuccessMessage('The file was successfully generated and downloaded.');
          setSuccessDialogOpen(true);
          document.getElementById('error-container')!.innerText = ''; 

          setTimeout(handleImageButtonClick, 2000); 

        } else {
          const errorMessage = await response.text();
          setErrorDialogMessage('Errors have been found');
          setErrorDialogOpen(true);
          let formattedError = errorMessage.replace(/[\[\]"]/g, '').replace(/,/g, '\n');
          formattedError = 'Errors:\n' + formattedError;
          document.getElementById('error-container')!.innerText = formattedError; 

          const imgElement = document.getElementById('imagen-scrollable') as HTMLImageElement | null;
          if (imgElement) {
            imgElement.src = "../public/imagen_error.jpeg"; 
          }

        }
      } catch (error) {
        setErrorDialogMessage('Error in the request.');
        setErrorDialogOpen(true);
      }
    }
  };

  const handleImageButtonClick = async () => {
    if (editorRef.current) {
      const contenidoEditor = editorRef.current.getValue();
      const service = new RdevsLangService();
      const syntaxErrors = service.validate(contenidoEditor);

      if (syntaxErrors.length > 0) {
        setErrorDialogMessage('The content contains syntax errors. Please correct them before obtaining the image.');
        setErrorDialogOpen(true);
        document.getElementById('error-container')!.innerText = ''; 
        return;
      }

      try {
        const response = await fetch(`${BASE_URL}/mensaje/crear-imagen`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ contenido: contenidoEditor }),
        });

        if (response.ok) {
          const blob = await response.blob();

          // Convertir blob a base64
          const reader = new FileReader();
          reader.onload = () => {
            const base64data = reader.result as string;
            setImageBase64(base64data);
            const imgElement = document.getElementById('imagen-scrollable') as HTMLImageElement | null;
            if (imgElement) {
              imgElement.src = base64data; 
            }
          };
          reader.readAsDataURL(blob);
          setSuccessMessage('The image was generated successfully.');
          setSuccessDialogOpen(true);
          document.getElementById('error-container')!.innerText = ''; 
        } else {
          const errorMessage = await response.text();
          setErrorDialogMessage('Errors have been found');
          setErrorDialogOpen(true);
          let formattedError = errorMessage.replace(/[\[\]"]/g, '').replace(/,/g, '\n');
          formattedError = 'Errors:\n' + formattedError;
          document.getElementById('error-container')!.innerText = formattedError; 

          const imgElement = document.getElementById('imagen-scrollable') as HTMLImageElement | null;
          if (imgElement) {
            imgElement.src = "../public/imagen_error.jpeg"; 
          }

        }
      } catch (error) {
        setErrorDialogMessage('Error in the request.');
        setErrorDialogOpen(true);
      }
    }
  };

  const handleCloseErrorDialog = () => {
    setErrorDialogOpen(false);
  };

  const handleCloseSuccessDialog = () => {
    setSuccessDialogOpen(false);
  };

  return (
    <div style={{ position: 'relative', width: '100%' }}>
      <div id="editor" style={{ width: '100%', height: '600px' }}></div>
      <ErrorDialog open={errorDialogOpen} message={errorDialogMessage} onClose={handleCloseErrorDialog} />
      <SuccessDialog open={successDialogOpen} message={successMessage} onClose={handleCloseSuccessDialog} />
    </div>
  );
};

interface IDialogProps {
  open: boolean;
  message: string;
  onClose: () => void;
}

const ErrorDialog: React.FC<IDialogProps> = ({ open, message, onClose }) => (
  <div
    style={{
      position: 'fixed',
      top: 0,
      left: 0,
      width: '100%',
      height: '100%',
      backgroundColor: 'rgba(0, 0, 0, 0.5)', // Fondo oscuro semi-opaco
      zIndex: 9999,
      display: open ? 'flex' : 'none',
      justifyContent: 'center',
      alignItems: 'center',
    }}
  >
    <dialog
      open={open}
      style={{
        position: 'relative',
        border: '1px solid #dc3545', 
        borderRadius: '5px',
        maxWidth: '100%',
        width: 'auto',
        background: 'white',
        textAlign: 'center',
        margin: '10px'
      }}
    >
      <div>{message}</div>
      <button
        style={{
          marginTop: '10px',
          cursor: 'pointer',
          backgroundColor: '#dc3545',
          color: 'white', 
          border: 'none',
          borderRadius: '5px',
          padding: '10px 20px',
        }}
        onClick={onClose}
      >
        Close
      </button>
    </dialog>
  </div>
);

const SuccessDialog: React.FC<IDialogProps> = ({ open, message, onClose }) => (
  <div
    style={{
      position: 'fixed',
      top: 0,
      left: 0,
      width: '100%',
      height: '100%',
      backgroundColor: 'rgba(0, 0, 0, 0.5)', 
      zIndex: 9999,
      display: open ? 'flex' : 'none',
      justifyContent: 'center',
      alignItems: 'center',
    }}
  >
    <dialog
      open={open}
      style={{
        position: 'relative',
        border: '1px solid #28a745', 
        borderRadius: '5px',
        maxWidth: '90%',
        width: '400px',
        background: 'white',
        padding: '20px',
        textAlign: 'center',
        color: '#28a745', 
      }}
    >
      <div>{message}</div>
      <button
        style={{
          marginTop: '10px',
          cursor: 'pointer',
          backgroundColor: '#28a745', // Fondo verde para el botón
          color: 'white', // Texto blanco para el botón
          border: 'none',
          borderRadius: '5px',
          padding: '10px 20px',
        }}
        onClick={onClose}
      >
        Close
      </button>
    </dialog>
  </div>
);

export { Editor };
