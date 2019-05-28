
import ImagePreviewer from '../src/main/resources/META-INF/resources/preview/js/ImagePreviewer.es';


let imagePreviewer;

describe(
	'document-library-preview-image',
	() => {

		// it('debug', () => {
		// 	console.log(process.env)
		// 	console.log(document.body);

		// 	var d = document.createElement('div');
		// 	d.innerHTML = 'hola';
		// 	document.body.appendChild(d);

		// 	expect(true).toBe(true)
		// });

		it('should render a creation image previwer', () => {

			const imagePreviewer = new ImagePreviewer(
				{
					spritemap: 'icons.svg',
					imageURL: 'image.jpg',
				},
				document.body
			);

			expect(imagePreviewer).toMatchSnapshot();
		});

	}
);